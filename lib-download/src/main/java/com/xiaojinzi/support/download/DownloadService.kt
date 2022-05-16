package com.xiaojinzi.support.download

import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.support.download.bean.DownloadCompletedTask
import com.xiaojinzi.support.download.bean.DownloadFailTask
import com.xiaojinzi.support.download.bean.DownloadProgressTask
import com.xiaojinzi.support.download.bean.DownloadTask
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.toObservable

interface DownloadService {

    /**
     * 提交一个任务
     */
    fun commit(task: DownloadTask)

    /**
     * 提交多个任务
     */
    fun commit(tasks: List<DownloadTask>)

    /**
     * 取消一个任务
     */
    fun cancel(tag: String)

    /**
     * 下载一个任务.
     */
    @ColdObservable
    fun download(task: DownloadTask): Single<DownloadCompletedTask>

    /**
     * 多任务下载, 信号是进度
     */
    @ColdObservable
    fun downloadProgress(task: DownloadTask): Observable<Float> {
        return Observable.create { emitter ->
            val disposables = CompositeDisposable()
            if (emitter.isDisposed) {
                return@create
            }
            emitter.setCancellable {
                disposables.dispose()
            }
            subscribePublishProgress(task.tag)
                .subscribeBy { progressTask ->
                    if (!emitter.isDisposed) {
                        emitter.onNext(progressTask.progress)
                    }
                }
                .addTo(disposables)
            download(task)
                .subscribeBy(
                    onSuccess = {
                        if (!emitter.isDisposed) {
                            emitter.onComplete()
                        }
                        disposables.dispose()
                    },
                    onError = {
                        if (!emitter.isDisposed) {
                            emitter.onError(it)
                        }
                        disposables.dispose()
                    }
                )
                .addTo(disposables)
        }
    }

    /**
     * 下载多个任务. 一个失败既表示全部失败
     */
    @ColdObservable
    fun downloadList(tasks: List<DownloadTask>): Single<List<DownloadCompletedTask>>

    /**
     * 多任务下载, 信号是进度
     */
    @ColdObservable
    fun downloadListProgress(tasks: List<DownloadTask>): Observable<Float> {
        return Observable.create { emitter ->
            val disposables = CompositeDisposable()
            if (emitter.isDisposed) {
                return@create
            }
            emitter.setCancellable {
                disposables.dispose()
            }
            subscribePublishCombineProgress(tasks.map { it.tag })
                .subscribeBy {
                    if (!emitter.isDisposed) {
                        emitter.onNext(it)
                    }
                }
                .addTo(disposables)
            downloadList(tasks)
                .subscribeBy(
                    onSuccess = {
                        if (!emitter.isDisposed) {
                            emitter.onComplete()
                        }
                        disposables.dispose()
                    },
                    onError = {
                        if (!emitter.isDisposed) {
                            emitter.onError(it)
                        }
                        disposables.dispose()
                    }
                )
                .addTo(disposables)
        }
    }

    /**
     * 订阅完成事件
     */
    @HotObservable(HotObservable.Pattern.PUBLISH)
    fun subscribePublishCompleted(tag: String? = null): Observable<DownloadCompletedTask>

    /**
     * 订阅进度事件
     */
    @HotObservable(HotObservable.Pattern.PUBLISH)
    fun subscribePublishProgress(tag: String? = null): Observable<DownloadProgressTask>

    /**
     * 监听多个任务的整体进度
     */
    @HotObservable(HotObservable.Pattern.PUBLISH)
    fun subscribePublishCombineProgress(tags: List<String>): Observable<Float> {
        return tags
            // tag 转化为对应的进度 Observable
            .map { tag ->
                subscribePublishProgress()
                    .filter { progressTask -> progressTask.task.tag == tag }
            }
            // 集合转 cold Observable
            .toObservable()
            // 收集一下, 变为 Single<List<Observable<DownloadProgressTask>>>
            .toList()
            // 转化 list 信号为另一组 Observable
            .flatMapObservable { observables ->
                // 组合 list 中各个 Observable 的进度. 输出总进度
                Observable.combineLatest(observables) { progressValues ->
                    // 累计计算总进度
                    val totalProgress: Float = progressValues
                        .map { it as DownloadProgressTask }
                        .map { it.progress }
                        .reduceOrNull { t1, t2 -> t1 + t2 } ?: 0f
                    // 算出最终的进度
                    totalProgress / progressValues.size
                }
            }
    }

    /**
     * 订阅失败事件
     */
    @HotObservable(HotObservable.Pattern.PUBLISH)
    fun subscribePublishFail(tag: String? = null): Observable<DownloadFailTask>

}