package com.xiaojinzi.support.download

import androidx.annotation.Keep
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority.MEDIUM
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.DownloadListener
import com.xiaojinzi.support.download.bean.*
import com.xiaojinzi.support.ktx.subscribeOnIOThread
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.toObservable
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

class DownloadServiceImpl : DownloadService {

    // 所有文件下载的进度数据. 这个只要拿到订阅之后的数据即可
    private val progressSubject = PublishSubject.create<DownloadProgressTask>()

    // 所有文件下载的完成的数据
    private val downloadCompleteSubject = PublishSubject.create<DownloadCompletedTask>()

    // 所有文件下载的错误的数据
    private val downloadFailSubject = PublishSubject.create<DownloadFailTask>()

    // 所有的任务, 线程安全
    private val map: MutableMap<String, DownloadTask> = ConcurrentHashMap()

    // lock
    private val downloadDirLock = Object()

    override fun commit(task: DownloadTask) {
        download(task)
            .ignoreElement()
            .onErrorComplete()
            .subscribe()
    }

    override fun commit(tasks: List<DownloadTask>) {
        tasks.forEach { commit(it) }
    }

    override fun isDownloading(tag: String): Boolean {
        return map.containsKey(key = tag)
    }

    override fun cancel(tag: String) {
        map.remove(tag)
            ?.let {
                // 取消
                AndroidNetworking.cancel(tag)
                downloadFailSubject.onNext(
                    DownloadFailTask(
                        it,
                        DownloadFailException("task cancelled, it tag is '$tag'")
                    )
                )
            }
    }

    override fun download(task: DownloadTask): Single<DownloadCompletedTask> {
        return download(DownloadTaskExtend(task = task)).map { it }
    }

    override fun downloadList(tasks: List<DownloadTask>): Single<List<DownloadCompletedTask>> {
        return tasks
            .mapIndexed { index, task -> DownloadTaskExtend(index = index, task = task) }
            .toObservable()
            .map { download(it) }
            .toList()
            .flatMapObservable {
                Single.merge(it)
                    .toObservable()
            }
            // 对结果排序
            .sorted { o1, o2 -> o1.index - o2.index }
            // 然后收集为 List
            .toList()
            // 调整结果的类型
            .map { it.toList() }
    }

    override fun subscribePublishCompleted(tag: String?): Observable<DownloadCompletedTask> {
        return if (tag.isNullOrEmpty()) {
            downloadCompleteSubject.subscribeOnIOThread()
        } else {
            downloadCompleteSubject
                .filter { it.task.tag == tag }
                .subscribeOnIOThread()
        }
    }

    override fun subscribePublishProgress(tag: String?): Observable<DownloadProgressTask> {
        return if (tag.isNullOrEmpty()) {
            progressSubject.subscribeOnIOThread()
        } else {
            progressSubject
                .filter { it.task.tag == tag }
                .subscribeOnIOThread()
        }
    }

    override fun subscribePublishFail(tag: String?): Observable<DownloadFailTask> {
        return if (tag.isNullOrEmpty()) {
            downloadFailSubject.subscribeOnIOThread()
        } else {
            downloadFailSubject
                .filter { it.task.tag == tag }
                .subscribeOnIOThread()
        }
    }

    /**
     * 下载一个文件. 这是基础
     */
    private fun download(task: DownloadTaskExtend): Single<DownloadCompletedTaskExtend> {
        if (task.retryCount < 0) {
            throw DownloadFailException("retryCount ${task.retryCount} < 0")
        }
        // 重试的 index
        val retryCountIndex = AtomicInteger()
        return Single
            .create<DownloadCompletedTaskExtend> { emitter ->
                // index
                val retryIndex = retryCountIndex.incrementAndGet()
                val disposables = CompositeDisposable()
                if (emitter.isDisposed) {
                    return@create
                }
                emitter.setCancellable {
                    disposables.dispose()
                    if (retryIndex == retryCountIndex.get()) {
                        cancel(task.tag)
                    }
                }
                subscribePublishCompleted()
                    .filter { it.task.tag == task.tag }
                    .subscribeOnIOThread()
                    .subscribeBy {
                        if (!emitter.isDisposed) {
                            emitter.onSuccess(DownloadCompletedTaskExtend(task.index, it))
                        }
                        disposables.dispose()
                    }
                    .addTo(disposables)
                subscribePublishFail()
                    .filter { it.task.tag == task.tag }
                    .subscribeOnIOThread()
                    .subscribeBy {
                        if (!emitter.isDisposed) {
                            emitter.onError(it.error)
                        }
                        disposables.dispose()
                    }
                    .addTo(disposables)
                realDownload(task)
            }
            // 重试的次数
            .retry(task.retryCount.toLong())
            // 在 IO 上订阅
            .subscribeOnIOThread()
    }

    private fun realDownload(task: DownloadTaskExtend) {
        synchronized(downloadDirLock) {
            task.downloadTo.parentFile?.let {
                if (!it.exists() || !it.isDirectory) {
                    val b = it.mkdirs()
                    if (!b) {
                        val error = DownloadFailException(message = "创建目录失败")
                        val downloadFailTask = DownloadFailTask(task, error)
                        downloadFailSubject.onNext(downloadFailTask)
                        return
                    }
                }
            }
        }
        if (map.containsKey(task.tag)) {
            downloadFailSubject.onNext(
                DownloadFailTask(
                    task,
                    DownloadFailException(message = "任务重复提交")
                )
            )
            return
        }
        map[task.tag] = task
        // 先走一个 0 进度的
        progressSubject.onNext(DownloadProgressTask(task, 0f))
        AndroidNetworking.download(task.url, task.downloadTo.parentFile.path, task.downloadTo.name)
            .setPriority(MEDIUM)
            .setTag(task.tag)
            .build()
            .setDownloadProgressListener { bytesDownloaded: Long, totalBytes: Long ->
                map[task.tag]?.let {
                    var progress = 0
                    if (totalBytes != 0L) {
                        progress = (bytesDownloaded * 100 / totalBytes).toInt()
                    }
                    if (progress < 0) {
                        progress = 0
                    } else if (progress > 100) {
                        progress = 100
                    }
                    progressSubject.onNext(DownloadProgressTask(task, progress.toFloat()))
                }
            }
            .startDownload(object : DownloadListener {
                override fun onDownloadComplete() {
                    map.remove(task.tag)
                        ?.let {
                            downloadCompleteSubject.onNext(DownloadCompletedTask(task))
                        }
                }

                override fun onError(anError: ANError) {
                    map.remove(task.tag)
                        ?.let {
                            downloadFailSubject.onNext(
                                DownloadFailTask(
                                    task,
                                    DownloadFailException(cause = anError)
                                )
                            )
                        }
                }
            })
    }

    @Keep
    class DownloadTaskExtend(
        // 下标
        val index: Int = -1,
        // 具体的任务
        task: DownloadTask
    ) : DownloadTask(task)

    @Keep
    class DownloadCompletedTaskExtend(
        val index: Int = -1,
        downloadCompletedTask: DownloadCompletedTask
    ) : DownloadCompletedTask(downloadCompletedTask.task)

}