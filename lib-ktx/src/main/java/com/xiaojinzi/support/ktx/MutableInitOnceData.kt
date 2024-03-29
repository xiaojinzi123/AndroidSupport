package com.xiaojinzi.support.ktx

import androidx.annotation.Keep
import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.support.annotation.NeedToOptimize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * 初始化一次的功能的接口
 */
interface InitOnceData<T> {

    /**
     * 当前值, 会抛出异常
     */
    val value: T

    /**
     * 是否初始化了
     */
    val isInit: Boolean

    /**
     * state 流
     */
    @NeedToOptimize("好像不必暴露 MutableSharedFlow 的, 返回 Flow 即可")
    val valueStateFlow: MutableSharedFlow<T>

    /**
     * 等待初始化值
     */
    suspend fun awaitValue(): T

}

/**
 * 扩展接口, 支持 value 可被写
 */
interface MutableInitOnceData<T> : InitOnceData<T> {

    /**
     * 当前的值, 可以赋值
     */
    override var value: T

}

/**
 * 初始化一次的一个数据结构
 */
@Keep
private class MutableInitOnceDataImpl<T>(
    val valueCheck: (T) -> Unit = {},
) : MutableInitOnceData<T> {

    private var _value: MutableSharedFlow<T> = MutableSharedFlow(
        replay = 1,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.SUSPEND,
    )

    // 对外的 get set
    override var value: T
        @Synchronized
        get() {
            if (_value.replayCache.isEmpty()) {
                error("you must init first!")
            }
            return _value.replayCache.first()
        }
        @Synchronized
        set(targetValue) {
            if (_value.replayCache.isNotEmpty()) {
                error("already init")
            }
            valueCheck(targetValue)
            _value.tryEmit(value = targetValue)
        }

    /**
     * 是否初始化了
     */
    override val isInit: Boolean
        @Synchronized
        get() = _value.replayCache.isNotEmpty()

    // 信号流
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    override val valueStateFlow = _value

    /**
     * 等待初始化的值.
     */
    override suspend fun awaitValue(): T = valueStateFlow.first()

}

@Suppress("FunctionName", "UNCHECKED_CAST")
fun <T> MutableInitOnceData(
    valueCheck: (T) -> Unit = {},
): MutableInitOnceData<T> {
    return MutableInitOnceDataImpl<T>(
        valueCheck = valueCheck,
    )
}

@Suppress("FunctionName", "UNCHECKED_CAST")
fun <T> MutableInitOnceData(
    initValue: T,
    valueCheck: (T) -> Unit = {},
): MutableInitOnceData<T> {
    return MutableInitOnceDataImpl<T>(
        valueCheck = valueCheck,
    ).apply {
        this.value = initValue
    }
}

fun <T, R> InitOnceData<T>.initOnceData(
    scope: CoroutineScope,
    valueCheck: (R) -> Unit = {},
    mapper: suspend (value: T) -> R,
): InitOnceData<R> {
    val upFlow = this.valueStateFlow
    val result = MutableInitOnceData<R>(
        valueCheck = valueCheck,
    )
    scope.launch {
        result.value = mapper(upFlow.first())
    }
    return result
}