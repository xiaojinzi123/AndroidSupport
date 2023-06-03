package com.xiaojinzi.support.ktx

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * 基于 [SharedFlow] 自己实现的 [StateFlow]
 * 名字叫 SharedStateFlow 是要和系统的 [StateFlow] 区分开来
 */
interface SharedStateFlow<T> : SharedFlow<T> {

    /**
     * 缓存的值
     */
    val value: T

    /**
     * 是否初始化了
     */
    val isInit: Boolean

}

interface MutableSharedStateFlow<T> : SharedStateFlow<T>, MutableSharedFlow<T> {

    /**
     * 缓存的值
     */
    override var value: T

    /**
     * 重放
     */
    fun replay()

}

private class MutableSharedStateFlowImpl<T>(
    val target: MutableSharedFlow<T>,
    val distinctUntilChanged: Boolean = false,
) : MutableSharedStateFlow<T>, MutableSharedFlow<T> by target {

    override var value: T
        get() {
            synchronized(this) {
                if (target.replayCache.isEmpty()) {
                    error("you must set value first")
                }
                return target.replayCache.first()
            }
        }
        set(targetValue) {
            synchronized(this) {
                if (distinctUntilChanged && isInit && targetValue == value) {
                    return@synchronized
                }
                target.tryEmit(value = targetValue)
            }
        }

    override val isInit: Boolean
        get() = target.replayCache.isNotEmpty()

    override fun replay() {
        this.value = this.value
    }

}

@Suppress("FunctionName", "UNCHECKED_CAST")
fun <T> MutableSharedStateFlow(
    distinctUntilChanged: Boolean = false,
): MutableSharedStateFlow<T> {
    val shareFlow = MutableSharedFlow<T>(
        replay = 1,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.SUSPEND
    )
    return MutableSharedStateFlowImpl(
        target = shareFlow,
        distinctUntilChanged = distinctUntilChanged,
    )
}

@Suppress("FunctionName", "UNCHECKED_CAST")
fun <T> MutableSharedStateFlow(
    initValue: T,
    distinctUntilChanged: Boolean = false,
): MutableSharedStateFlow<T> {
    val shareFlow = MutableSharedFlow<T>(
        replay = 1,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.SUSPEND
    )
    val result = shareFlow.tryEmit(value = initValue)
    if (!result) {
        error("MutableSharedStateFlow Fail to emit initValue")
    }
    return MutableSharedStateFlowImpl(
        target = shareFlow,
        distinctUntilChanged = distinctUntilChanged,
    )
}

private fun <T> Flow<T>.doSharedStateIn(
    shareFlow: MutableSharedStateFlow<T>,
    scope: CoroutineScope,
    isTakeOne: Boolean = false,
    dropIfValueIsSet: Boolean = false,
) {
    val upstream = this
    scope.launch {
        if (isTakeOne) {
            val upstreamFirstValue = upstream.first()
            if (dropIfValueIsSet) {
                if (!shareFlow.isInit) {
                    shareFlow.value = upstreamFirstValue
                }
            } else {
                shareFlow.value = upstreamFirstValue
            }
            this.cancel()
        } else {
            upstream.collect {
                if (dropIfValueIsSet) {
                    if (!shareFlow.isInit) {
                        shareFlow.value = it
                    }
                    this.cancel()
                } else {
                    shareFlow.value = it
                }
            }
        }
    }
}

/**
 * @param isTakeOne 是否对上流的信号只取一个
 * @param dropIfValueIsSet 如果值已经设置，是否忽略掉
 * @param distinctUntilChanged 是否去重
 */
fun <T> Flow<T>.mutableSharedStateIn(
    scope: CoroutineScope,
    isTakeOne: Boolean = false,
    dropIfValueIsSet: Boolean = false,
    distinctUntilChanged: Boolean = false,
): MutableSharedStateFlow<T> {
    val shareFlow = MutableSharedStateFlow<T>(
        distinctUntilChanged = distinctUntilChanged,
    )
    this.doSharedStateIn(
        shareFlow = shareFlow,
        scope = scope,
        isTakeOne = isTakeOne,
        dropIfValueIsSet = dropIfValueIsSet,
    )
    return shareFlow
}

fun <T> Flow<T>.sharedStateIn(
    scope: CoroutineScope,
    isTakeOne: Boolean = false,
    dropIfValueIsSet: Boolean = false,
    distinctUntilChanged: Boolean = false,
): SharedStateFlow<T> {
    return this.mutableSharedStateIn(
        scope = scope,
        isTakeOne = isTakeOne,
        dropIfValueIsSet = dropIfValueIsSet,
        distinctUntilChanged = distinctUntilChanged,
    )
}

/**
 * 这里为什么要塞一个默认值. 是因为对于 Behavior 模式来说, 一定要有一个值. 即使这个值是 null
 * 为什么 RxJava 的 Behavior 模式可以不用给默认值, 是因为你去取 value 的时候默认是 null 呀
 * RxJava 不允许信号的值是 null, 所以这种特殊情况 RxJava 是可以区别出来的. 但是 Flow 是不可以的
 * 因为 Flow 的信号是可以为空的, 这也导致了默认 null 可能会和用户要表示的值的类型发生冲突. 所以这里一定要
 * 用户把值塞进来, 就算是一个 null
 */
fun <T> Flow<T>.mutableSharedStateIn(
    initValue: T,
    scope: CoroutineScope,
    isTakeOne: Boolean = false,
    dropIfValueIsSet: Boolean = false,
    distinctUntilChanged: Boolean = false,
): MutableSharedStateFlow<T> {
    val shareFlow = MutableSharedStateFlow(
        initValue = initValue,
        distinctUntilChanged = distinctUntilChanged,
    )
    this.doSharedStateIn(
        shareFlow = shareFlow,
        scope = scope,
        isTakeOne = isTakeOne,
        dropIfValueIsSet = dropIfValueIsSet,
    )
    return shareFlow
}

fun <T> Flow<T>.sharedStateIn(
    initValue: T,
    scope: CoroutineScope,
    isTakeOne: Boolean = false,
    dropIfValueIsSet: Boolean = false,
    distinctUntilChanged: Boolean = false,
): SharedStateFlow<T> {
    return this.mutableSharedStateIn(
        initValue = initValue,
        scope = scope,
        isTakeOne = isTakeOne,
        dropIfValueIsSet = dropIfValueIsSet,
        distinctUntilChanged = distinctUntilChanged,
    )
}

// 针对 Boolean 的扩展
fun MutableSharedStateFlow<Boolean>.tryToggle() {
    this.value = !this.value
}

@Deprecated(
    message = "Use tryToggle instead",
    replaceWith = ReplaceWith("tryToggle()"),
)
fun MutableSharedStateFlow<Boolean>.toggle() {
    this.value = !this.value
}

suspend fun MutableSharedStateFlow<Boolean>.toggleBySuspend() {
    this.emit(
        value = !this.value
    )
}

// 针对 String 的扩展
fun MutableSharedStateFlow<String>.tryEmpty() {
    this.value = ""
}

suspend fun MutableSharedStateFlow<String>.emptyBySuspend() {
    this.emit(value = "")
}
