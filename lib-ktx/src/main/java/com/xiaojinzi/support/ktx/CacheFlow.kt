package com.xiaojinzi.support.ktx

import com.xiaojinzi.support.annotation.HotObservable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

interface CacheFlow<T> : Flow<T> {

    /**
     * 发射到缓存中, 后续一个不落的全部会被发出
     */
    fun add(value: T)

}

interface CacheStateFlow<T> : CacheFlow<T>

internal class CacheFlowImpl<T>(
    val valueCheck: (T) -> Unit = {},
    private val sharedFlow: NormalMutableSharedFlow<T>,
) : MutableSharedFlow<T> by sharedFlow, CacheFlow<T> {

    override fun add(value: T) {
        valueCheck(value)
        sharedFlow.add(value = value)
    }

}

internal class CacheStateFlowImpl<T>(
    val valueCheck: (T) -> Unit = {},
    private val sharedFlow: NormalMutableSharedFlow<T>,
) : MutableSharedFlow<T> by sharedFlow, CacheStateFlow<T> {

    override fun add(value: T) {
        valueCheck(value)
        sharedFlow.add(value = value)
    }

}

@Suppress("FunctionName")
@HotObservable(HotObservable.Pattern.PUBLISH, isShared = true)
fun <T> CacheSharedFlow(
    valueCheck: (T) -> Unit = {},
): CacheFlow<T> {
    return CacheFlowImpl(
        valueCheck = valueCheck,
        sharedFlow = NormalMutableSharedFlow(),
    )
}

@Suppress("FunctionName")
@HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
fun <T> CacheSharedStateFlow(
    valueCheck: (T) -> Unit = {},
): CacheStateFlow<T> {
    return CacheStateFlowImpl(
        valueCheck = valueCheck,
        sharedFlow = NormalMutableSharedFlow(
            replay = 1,
        ),
    )
}

@Suppress("FunctionName")
@HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
fun <T> CacheSharedStateFlow(
    valueCheck: (T) -> Unit = {},
    initValue: T,
): CacheStateFlow<T> {
    return CacheStateFlowImpl(
        valueCheck = valueCheck,
        sharedFlow = NormalMutableSharedFlow<T>(
            replay = 1,
        ).apply {
            this.add(
                value = initValue,
            )
        },
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