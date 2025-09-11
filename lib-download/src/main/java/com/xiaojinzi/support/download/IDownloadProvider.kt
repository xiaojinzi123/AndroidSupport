/*
 * Original Author: fanqie (xiaojinzi@vistring.com)
 * Created:       2024-10-31
 * Last Modified: 2024-11-26
 * Maintainer:    fanqie
 *
 * Copyright (c) 2025 Vistring Inc.
 */
package com.xiaojinzi.support.download

import androidx.annotation.CheckResult
import androidx.annotation.FloatRange
import androidx.annotation.Keep
import com.xiaojinzi.support.ktx.LogSupport
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import java.util.concurrent.CancellationException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume

/**
 * 各种平台的下载, 可能都需要携带不同的参数
 * 所以这个抽象接口提供了扩展的能力
 */
interface IDownloadTask {

    /**
     * 基础的任务对象
     */
    val basicTask: DownloadTask

}

/**
 * 下载的任务
 */
@Keep
sealed class DownloadTask @JvmOverloads constructor(
    open val tag: String = UUID.randomUUID().toString(),
    open val downloadTo: File,
    // 如果任务存在是否抛出异常
    open val throwIfExist: Boolean,
) : IDownloadTask {

    override val basicTask: DownloadTask
        get() = this

    data class Default(
        override val tag: String = UUID.randomUUID().toString(),
        override val downloadTo: File,
        override val throwIfExist: Boolean = true,
    ) : DownloadTask(
        tag = tag,
        downloadTo = downloadTo,
        throwIfExist = throwIfExist,
    )

    data class UrlDownloadTask(
        override val tag: String = UUID.randomUUID().toString(),
        override val downloadTo: File,
        override val throwIfExist: Boolean = true,
        val url: String,
    ) : DownloadTask(
        tag = tag,
        downloadTo = downloadTo,
        throwIfExist = throwIfExist,
    )

}

/**
 * 下载失败的异常
 */
typealias DownloadFailException = Exception

@Keep
sealed class DownloadState(
    open val task: IDownloadTask,
) {

    @Keep
    sealed class EndState(
        override val task: IDownloadTask,
    ) : DownloadState(
        task = task,
    ) {

        @Keep
        data class DownloadSuccess(
            override val task: IDownloadTask,
        ) : EndState(
            task = task,
        )

        @Keep
        data class DownloadFailed(
            override val task: IDownloadTask,
            val error: DownloadFailException,
        ) : EndState(
            task = task,
        )

    }

    @Keep
    data class ProgressState(
        override val task: IDownloadTask,
        val totalSize: Long?,
        val downloadSize: Long,
        @FloatRange(from = 0.0, to = 1.0)
        val progress: Float = if (totalSize == null || totalSize <= 0) {
            0f
        } else {
            if (downloadSize > totalSize) {
                1f
            } else {
                downloadSize.toFloat() / totalSize.toFloat()
            }
        },
    ) : DownloadState(
        task = task,
    )

}

@Keep
sealed class CombineDownloadState(
    open val tasks: List<IDownloadTask>,
) {

    @Keep
    sealed class EndState(
        override val tasks: List<IDownloadTask>,
    ) : CombineDownloadState(
        tasks = tasks,
    ) {

        @Keep
        data class DownloadSuccess(
            override val tasks: List<IDownloadTask>,
        ) : EndState(
            tasks = tasks,
        )

        @Keep
        data class DownloadFailed(
            override val tasks: List<IDownloadTask>,
            val error: DownloadFailException,
        ) : EndState(
            tasks = tasks,
        )

    }

    @Keep
    data class ProgressState(
        override val tasks: List<IDownloadTask>,
        val totalSize: Long,
        val downloadSize: Long,
        @FloatRange(from = 0.0, to = 1.0)
        val progress: Float = if (totalSize <= 0) {
            0f
        } else {
            if (downloadSize > totalSize) {
                1f
            } else {
                downloadSize.toFloat() / totalSize.toFloat()
            }
        },
    ) : CombineDownloadState(
        tasks = tasks,
    )

}

@Keep
data class CombinedProgressState(
    val tasks: List<IDownloadTask>,
    val totalSize: Long,
    val downloadSize: Long,
    @FloatRange(from = 0.0, to = 1.0)
    val progress: Float = if (totalSize <= 0) {
        0f
    } else {
        if (downloadSize > totalSize) {
            1f
        } else {
            downloadSize.toFloat() / totalSize.toFloat()
        }
    },
)

/**
 * 下载实现的接口
 * 实现可以基于任何形式的下载, [IDownloadProviderBaseImpl] 实现了下载过程中的大部分逻辑
 * 新的下载的可以直接继承 [IDownloadProviderBaseImpl]
 * 实现 [IDownloadProviderBaseImpl.doDownload] 和 [IDownloadProviderBaseImpl.cancel] 方法即可
 *
 * 1. [com.vistring.base.service.aws.AWSS3Provider] 下载可以迁移到此
 * 2. [com.vistring.foundation.network.BatchDownloader] 批量下载的也可以迁移到此
 * 3. [com.vistring.vlogger.android.data.PlayStoreAssetsManagerKt.download] 对 PlayStoreAssetsManager 下载的封装也可以迁移到此
 *
 */
interface IDownloadProvider {

    companion object {
        const val TAG = "IDownloadProvider"
    }

    /**
     * 下载一个任务
     */
    @CheckResult
    suspend fun download(task: IDownloadTask): DownloadState.EndState

    /**
     * 批量下载任务
     * 一个失败将会认为是所有任务都失败
     */
    @CheckResult
    suspend fun downloadList(tasks: List<IDownloadTask>): List<DownloadState.EndState>

    /**
     * 创建一个下载任务的 Observable
     * 这个流是冷的: Cold Observable
     * [DownloadState.ProgressState] --> [DownloadState.EndState]
     */
    @CheckResult
    fun createDownloadObservable(task: IDownloadTask): Flow<DownloadState>

    /**
     * 创建多个下载任务的 Observable
     * 这个流是冷的: Cold Observable
     */
    @CheckResult
    fun createDownloadListObservable(tasks: List<IDownloadTask>): Flow<CombineDownloadState>

    /**
     * 提交一个任务
     *
     * @return 任务的 tag
     */
    fun commitTask(task: IDownloadTask): String

    /**
     * 提交多个任务
     *
     * @return 任务的 tag
     */
    fun commitTasks(tasks: List<IDownloadTask>): List<String>

    /**
     * 取消一个任务
     */
    fun cancel(tag: String)

    /**
     * 是否包含此任务
     */
    fun containsTask(tag: String): Boolean

    /**
     * 订阅进度事件
     * 返回的流是热的：Hot Observable, State 模式
     */
    @CheckResult
    fun subscribeTaskTagState(): Flow<List<String>>

    /**
     * 订阅进度事件
     * 返回的流是热的：Hot Observable, Event 模式
     */
    @CheckResult
    fun subscribeProgressEvent(
        tag: String,
    ): Flow<DownloadState.ProgressState>

    /**
     * 订阅进度事件
     * 具有多个任务合并的进度功能
     * 返回的流是热的：Hot Observable, Event 模式
     */
    @CheckResult
    fun subscribeCombinedProgressEvent(
        vararg tags: String,
    ): Flow<CombinedProgressState>

    /**
     * 订阅下载成功事件
     * 返回的流是热的：Hot Observable, Event 模式
     */
    @CheckResult
    fun subscribeSuccessEvent(
        vararg tags: String,
    ): Flow<DownloadState.EndState.DownloadSuccess>

    /**
     * 订阅下载成功事件
     * 返回的流是热的：Hot Observable, Event 模式
     */
    @CheckResult
    fun subscribeSuccessEvent(): Flow<DownloadState.EndState.DownloadSuccess>

    /**
     * 订阅下载失败事件
     * 返回的流是热的：Hot Observable, Event 模式
     */
    @CheckResult
    fun subscribeFailEvent(
        vararg tags: String,
    ): Flow<DownloadState.EndState.DownloadFailed>

}

abstract class IDownloadProviderBaseImpl : IDownloadProvider {

    // 弄一个单线程的线程池
    private val DefaultExecutors = Executors.newSingleThreadExecutor()

    // 单线程池的协程调度器
    private val DefaultDispatcher = DefaultExecutors.asCoroutineDispatcher()

    private fun createContextScope(
        context: CoroutineContext? = null,
    ): CoroutineScope {
        return object : CoroutineScope {
            override val coroutineContext: CoroutineContext =
                SupervisorJob() + (context ?: DefaultDispatcher)

            // CoroutineScope is used intentionally for user-friendly representation
            override fun toString(): String = "CoroutineScope(coroutineContext=$coroutineContext)"
        }
    }

    // 所有的任务, 线程安全
    private val taskMap: MutableMap<String, IDownloadTask> = ConcurrentHashMap()

    // 存在的任务的 tag 列表
    private val taskTagState = MutableSharedFlow<List<String>>(
        replay = 1,
        extraBufferCapacity = Int.MAX_VALUE,
    )

    /**
     * 进度的事件, 缓存无限制, tryEmit 一定会成功
     */
    private val progressEvent = MutableSharedFlow<DownloadState.ProgressState>(
        extraBufferCapacity = Int.MAX_VALUE,
    )

    // 所有文件下载的完成的数据
    private val downloadSuccessEvent = MutableSharedFlow<DownloadState.EndState.DownloadSuccess>(
        extraBufferCapacity = Int.MAX_VALUE,
    )

    // 所有文件下载的错误的数据
    private val downloadFailEvent = MutableSharedFlow<DownloadState.EndState.DownloadFailed>(
        extraBufferCapacity = Int.MAX_VALUE,
    )

    private fun updateTaskTagState() {
        taskTagState.tryEmit(
            value = taskMap.map { it.key },
        )
    }

    /**
     * 实现类必须实现这个方法
     * 完成真正的下载逻辑
     * 然后通过过 [postProgress] [postDownloadSuccess] [postDownloadFail] 等方法发出对应事件
     */
    protected abstract fun doDownload(task: IDownloadTask)

    /**
     * 实现类必须实现这个方法
     * 取消下载的任务
     */
    protected abstract fun doCancel(task: IDownloadTask)

    protected fun postProgress(
        progress: DownloadState.ProgressState,
    ) {
        DefaultExecutors.submit {
            /*Log.d(
                tag = Log.Tag.Network,
                msg = "${IDownloadProvider.TAG} Download progress, task: ${progress.task}, progress: ${progress.progress}",
            )*/
            progressEvent.tryEmit(value = progress)
        }
    }

    protected fun postDownloadSuccess(
        success: DownloadState.EndState.DownloadSuccess,
    ) {
        DefaultExecutors.submit {
            LogSupport.d(
                tag = IDownloadProvider.TAG,
                content = "${IDownloadProvider.TAG} Download success, task: ${success.task}",
            )
            taskMap.remove(key = success.task.basicTask.tag)?.run {
                updateTaskTagState()
                downloadSuccessEvent.tryEmit(value = success)
            }
        }
    }

    protected fun postDownloadFail(
        fail: DownloadState.EndState.DownloadFailed,
    ) {
        DefaultExecutors.submit {
            LogSupport.d(
                tag = IDownloadProvider.TAG,
                content = "${IDownloadProvider.TAG} Download fail, task: ${fail.task}, error: ${fail.error.message}",
            )
            taskMap.remove(key = fail.task.basicTask.tag)?.run {
                updateTaskTagState()
                downloadFailEvent.tryEmit(value = fail)
            }
        }
    }

    override suspend fun download(task: IDownloadTask): DownloadState.EndState {
        return withContext(context = Dispatchers.Default) {
            suspendCancellableCoroutine { cancellableContinuation ->
                val tempScope = createContextScope()
                downloadSuccessEvent
                    .filter { it.task == task }
                    .onEach {
                        if (!cancellableContinuation.isCompleted) {
                            cancellableContinuation.resume(
                                value = it,
                            )
                        }
                        tempScope.cancel()
                    }.launchIn(scope = tempScope)
                downloadFailEvent
                    .filter { it.task == task }
                    .onEach {
                        if (!cancellableContinuation.isCompleted) {
                            cancellableContinuation.resume(
                                value = it,
                            )
                        }
                        tempScope.cancel()
                    }.launchIn(scope = tempScope)
                cancellableContinuation.invokeOnCancellation {
                    tempScope.cancel()
                    cancel(tag = task.basicTask.tag)
                }
                // 提交任务
                commitTask(task = task)
            }
        }
    }

    override suspend fun downloadList(tasks: List<IDownloadTask>): List<DownloadState.EndState> {
        return runCatching {
            withContext(context = Dispatchers.Default) {
                tasks
                    .map {
                        async(context = Dispatchers.Default) {
                            download(task = it)
                        }
                    }
                    .awaitAll()
            }
        }.onFailure {
            tasks.forEach {
                cancel(tag = it.basicTask.tag)
            }
        }.getOrThrow()
    }

    override fun createDownloadObservable(task: IDownloadTask): Flow<DownloadState> {
        return channelFlow {
            val producerScope = this
            val tempScope = createContextScope()
            downloadSuccessEvent
                .filter { it.task == task }
                .onEach {
                    tempScope.cancel()
                    if (producerScope.isActive) {
                        producerScope.send(element = it)
                        producerScope.close()
                    }
                }.launchIn(scope = tempScope)
            downloadFailEvent
                .filter { it.task == task }
                .onEach {
                    tempScope.cancel()
                    if (producerScope.isActive) {
                        producerScope.send(element = it)
                        producerScope.close()
                    }
                }.launchIn(scope = tempScope)
            progressEvent
                .filter { it.task == task }
                .onEach {
                    if (producerScope.isActive) {
                        producerScope.send(element = it)
                    }
                }.launchIn(scope = tempScope)
            // 提交任务
            commitTask(task = task)
            awaitClose {
                tempScope.cancel()
            }
        }
    }

    override fun createDownloadListObservable(tasks: List<IDownloadTask>): Flow<CombineDownloadState> {
        return channelFlow {
            val producerScope = this
            val progressMap = HashMap<String, DownloadState.ProgressState>()
            val job = launch(context = Dispatchers.Default) {
                combine(
                    tasks.map { createDownloadObservable(it) },
                ) { stateList ->
                    val isAllSuccess =
                        stateList.all { it is DownloadState.EndState.DownloadSuccess }
                    for (downloadState in stateList) {
                        if (downloadState is DownloadState.ProgressState) {
                            progressMap[downloadState.task.basicTask.tag] = downloadState
                        }
                    }
                    if (isAllSuccess) {
                        CombineDownloadState.EndState.DownloadSuccess(
                            tasks = tasks,
                        )
                    } else {
                        val firstFailedItemList =
                            stateList.filterIsInstance<DownloadState.EndState.DownloadFailed>()
                        if (firstFailedItemList.isNotEmpty()) {
                            CombineDownloadState.EndState.DownloadFailed(
                                tasks = tasks,
                                error = firstFailedItemList.first().error,
                            )
                        } else {
                            CombineDownloadState.ProgressState(
                                tasks = tasks,
                                totalSize = progressMap.values.sumOf { it.totalSize ?: 0 },
                                downloadSize = progressMap.values.sumOf { it.downloadSize },
                            )
                        }
                    }
                }.collect {
                    if (producerScope.isActive) {
                        producerScope.send(element = it)
                        if (it is CombineDownloadState.EndState) {
                            channel.close()
                        }
                    }
                }
                progressMap.clear()
            }
            awaitClose {
                job.cancel()
            }
        }
    }

    override fun subscribeTaskTagState(): Flow<List<String>> {
        return taskTagState
    }

    override fun subscribeProgressEvent(tag: String): Flow<DownloadState.ProgressState> {
        return progressEvent
            .filter { it.task.basicTask.tag == tag }
            .distinctUntilChangedBy { it.progress }
            .flowOn(context = Dispatchers.Default)
    }

    override fun subscribeCombinedProgressEvent(vararg tags: String): Flow<CombinedProgressState> {
        return combine(
            tags
                .map {
                    subscribeProgressEvent(tag = it)
                }
        ) { progressStateList ->
            CombinedProgressState(
                tasks = progressStateList.map { it.task },
                totalSize = progressStateList.sumOf { it.totalSize ?: 0 },
                downloadSize = progressStateList.sumOf { it.downloadSize },
            )
        }
    }

    override fun subscribeSuccessEvent(vararg tags: String): Flow<DownloadState.EndState.DownloadSuccess> {
        return downloadSuccessEvent
            .filter { it.task.basicTask.tag in tags }
            .flowOn(context = Dispatchers.Default)
    }

    override fun subscribeSuccessEvent(): Flow<DownloadState.EndState.DownloadSuccess> {
        return downloadSuccessEvent
    }

    override fun subscribeFailEvent(vararg tags: String): Flow<DownloadState.EndState.DownloadFailed> {
        return downloadFailEvent
            .filter { it.task.basicTask.tag in tags }
            .flowOn(context = Dispatchers.Default)
    }

    /**
     * 提交下载任务
     */
    final override fun commitTask(task: IDownloadTask): String {
        LogSupport.d(
            tag = IDownloadProvider.TAG,
            content = "${IDownloadProvider.TAG} start Commit download task, task: ${task.basicTask}",
        )
        if (taskMap.containsKey(task.basicTask.tag)) {
            if (task.basicTask.throwIfExist) {
                throw IllegalArgumentException("Task with tag ${task.basicTask.tag} already exists")
            } else {
                LogSupport.d(
                    tag = IDownloadProvider.TAG,
                    content = "${IDownloadProvider.TAG} Task with tag ${task.basicTask.tag} already committed",
                )
                return task.basicTask.tag
            }
        }
        taskMap[task.basicTask.tag] = task.basicTask
        updateTaskTagState()
        doDownload(task = task)
        LogSupport.d(
            tag = IDownloadProvider.TAG,
            content = "${IDownloadProvider.TAG} Success Commit download task, task: ${task.basicTask}",
        )
        return task.basicTask.tag
    }

    final override fun commitTasks(tasks: List<IDownloadTask>): List<String> {
        return tasks.map { commitTask(task = it) }
    }

    final override fun cancel(tag: String) {
        taskMap.remove(tag)?.run {
            LogSupport.d(
                tag = IDownloadProvider.TAG,
                content = "${IDownloadProvider.TAG} Cancel download task, task: ${this.basicTask}",
            )
            updateTaskTagState()
            doCancel(task = this)
            postDownloadFail(
                fail = DownloadState.EndState.DownloadFailed(
                    task = this,
                    error = CancellationException("Cancelled by tag $tag"),
                )
            )
        }
    }

    final override fun containsTask(tag: String): Boolean {
        return taskMap.contains(tag)
    }

    init {
        updateTaskTagState()
    }

}