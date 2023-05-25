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
     * 发射到缓存中, 后续一个不落的全部会被发出
     */
    fun add(value: T)

}

internal class CacheFlowImpl<T>(
    val valueCheck: (T) -> Unit = {},
    private val sharedFlow: MutableSharedFlow<T>,
) : MutableSharedFlow<T> by sharedFlow, CacheFlow<T> {

    override fun add(value: T) {
        valueCheck(value)
        // 为什么这里可以使用 try 不会有阻塞的问题, 因为这里的 sharedFlow 是一个无限大的容量
        // 所以这里就不用做缓冲的处理了
        if (!sharedFlow.tryEmit(value = value)) {
            notSupportError()
        }
    }

}

@Suppress("FunctionName", "UNCHECKED_CAST")
@HotObservable(HotObservable.Pattern.PUBLISH, isShared = true)
fun <T> CacheSharedFlow(
    valueCheck: (T) -> Unit = {},
): CacheFlow<T> {
    val sharedFlow = NormalMutableSharedFlow<T>()
    return CacheFlowImpl(
        valueCheck = valueCheck,
        sharedFlow = sharedFlow,
    )
}

@Suppress("FunctionName", "UNCHECKED_CAST")
@HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
fun <T> CacheSharedStateFlow(
    valueCheck: (T) -> Unit = {},
): CacheFlow<T> {
    val sharedStateFlow = MutableSharedStateFlow<T>()
    return CacheFlowImpl(
        valueCheck = valueCheck,
        sharedFlow = sharedStateFlow,
    )
}

@Suppress("FunctionName", "UNCHECKED_CAST")
@HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
fun <T> CacheSharedStateFlow(
    valueCheck: (T) -> Unit = {},
    initValue: T,
): CacheFlow<T> {
    val sharedStateFlow = MutableSharedStateFlow(initValue = initValue)
    return CacheFlowImpl(
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