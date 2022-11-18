package com.xiaojinzi.support.ktx

import com.xiaojinzi.support.annotation.HotObservable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.LinkedBlockingQueue

interface CacheFlow<T> : Flow<T> {

    /**
     * 尝试发射
     */
    fun add(value: T)

}

internal class CacheFlowImpl<T>(
    scope: CoroutineScope,
    val valueCheck: (T) -> Unit = {},
    private val sharedFlow: MutableSharedFlow<T>,
) : MutableSharedFlow<T> by sharedFlow, CacheFlow<T> {

    private val queue = LinkedBlockingQueue<T>()

    override fun add(value: T) {
        valueCheck(value)
        queue.offer(value)
    }

    init {
        scope.launch(context = Dispatchers.IO) {
            while (isActive) {
                emit(value = queue.take())
            }
        }
    }

}

@Suppress("FunctionName", "UNCHECKED_CAST")
@HotObservable(HotObservable.Pattern.PUBLISH, isShared = true)
fun <T> CacheSharedFlow(
    scope: CoroutineScope,
    valueCheck: (T) -> Unit = {},
): CacheFlow<T> {
    val sharedFlow = NormalMutableSharedFlow<T>()
    return CacheFlowImpl(
        scope = scope,
        valueCheck = valueCheck,
        sharedFlow = sharedFlow,
    )
}

@Suppress("FunctionName", "UNCHECKED_CAST")
@HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
fun <T> CacheSharedStateFlow(
    scope: CoroutineScope,
    valueCheck: (T) -> Unit = {},
): CacheFlow<T> {
    val sharedStateFlow = MutableSharedStateFlow<T>()
    return CacheFlowImpl(
        scope = scope,
        valueCheck = valueCheck,
        sharedFlow = sharedStateFlow,
    )
}

@Suppress("FunctionName", "UNCHECKED_CAST")
@HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
fun <T> CacheSharedStateFlow(
    scope: CoroutineScope,
    valueCheck: (T) -> Unit = {},
    initValue: T,
): CacheFlow<T> {
    val sharedStateFlow = MutableSharedStateFlow(initValue = initValue)
    return CacheFlowImpl(
        scope = scope,
        valueCheck = valueCheck,
        sharedFlow = sharedStateFlow,
    )
}

// -------------------- 对应的几个扩展函数 --------------------

fun <T> Flow<T>.cacheSharedIn(
    scope: CoroutineScope,
    valueCheck: (T) -> Unit = {},
): CacheFlow<T> {
    val upstream = this
    val sharedFlow = CacheSharedFlow<T>(
        scope = scope,
        valueCheck = valueCheck,
    )
    upstream
        .onEach { item ->
            sharedFlow.add(value = item)
        }
        .launchIn(scope = scope)
    return sharedFlow
}

fun <T> Flow<T>.cacheSharedStateIn(
    scope: CoroutineScope,
    valueCheck: (T) -> Unit = {},
): CacheFlow<T> {
    val upstream = this
    val sharedFlow = CacheSharedStateFlow<T>(
        scope = scope,
        valueCheck = valueCheck,
    )
    upstream
        .onEach { item ->
            sharedFlow.add(value = item)
        }
        .launchIn(scope = scope)
    return sharedFlow
}

fun <T> Flow<T>.cacheSharedStateIn(
    scope: CoroutineScope,
    valueCheck: (T) -> Unit = {},
    initValue: T,
): CacheFlow<T> {
    val upstream = this
    val sharedFlow = CacheSharedStateFlow(
        scope = scope,
        valueCheck = valueCheck,
        initValue = initValue,
    )
    upstream
        .onEach { item ->
            sharedFlow.add(value = item)
        }
        .launchIn(scope = scope)
    return sharedFlow
}