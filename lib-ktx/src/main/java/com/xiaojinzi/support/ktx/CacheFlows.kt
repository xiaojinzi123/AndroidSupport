package com.xiaojinzi.support.ktx

import com.xiaojinzi.support.annotation.HotObservable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentLinkedQueue

interface CacheFlow<T> : Flow<T> {

    /**
     * 尝试发射
     */
    fun add(value: T)

}

internal class CacheFlowImpl<T>(
    private val scope: CoroutineScope,
    private val sharedFlow: MutableSharedFlow<T>
) : MutableSharedFlow<T> by sharedFlow, CacheFlow<T> {

    private val queue: ConcurrentLinkedQueue<T> = ConcurrentLinkedQueue()

    override fun add(value: T) {
        queue.offer(value)
    }

    init {
        scope.launch {
            while (isActive) {
                queue.poll()?.let {
                    emit(value = it)
                }
            }
        }
    }

}

@Suppress("FunctionName", "UNCHECKED_CAST")
@HotObservable(HotObservable.Pattern.PUBLISH, isShared = true)
fun <T> CacheSharedFlow(
    scope: CoroutineScope,
): CacheFlow<T> {
    val sharedFlow = NormalMutableSharedFlow<T>()
    return CacheFlowImpl(
        scope = scope,
        sharedFlow = sharedFlow,
    )
}

@Suppress("FunctionName", "UNCHECKED_CAST")
@HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
fun <T> CacheSharedStateFlow(
    scope: CoroutineScope,
): CacheFlow<T> {
    val sharedStateFlow = MutableSharedStateFlow<T>()
    return CacheFlowImpl(
        scope = scope,
        sharedFlow = sharedStateFlow,
    )
}

@Suppress("FunctionName", "UNCHECKED_CAST")
@HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
fun <T> CacheSharedStateFlow(
    scope: CoroutineScope,
    initValue: T,
): CacheFlow<T> {
    val sharedStateFlow = MutableSharedStateFlow(initValue = initValue)
    return CacheFlowImpl(
        scope = scope,
        sharedFlow = sharedStateFlow,
    )
}