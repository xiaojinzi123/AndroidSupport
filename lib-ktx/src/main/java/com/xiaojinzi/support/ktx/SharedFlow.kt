package com.xiaojinzi.support.ktx

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

private const val TAG = "SharedFlow"

/**
 * 一个普通的 [MutableSharedFlow] 接口
 */
interface NormalMutableSharedFlow<T>: MutableSharedFlow<T> {

    /**
     * 因为 tryEmit 方法在 NormalMutableSharedFlow 中是必然成功的.
     * 因为内部的 extraBufferCapacity 是无止境的
     * 但是调用方调用的时候可能有误解, 所以这里直接返回 NormalMutableSharedFlow 接口对象
     * 里面声明了 add 方法, 就不会有歧义
     */
    fun add(value: T) {
        this.tryEmit(
            value = value
        )
    }

}

private class NormalMutableSharedFlowImpl<T>(
    private val targetFlow: MutableSharedFlow<T>,
): NormalMutableSharedFlow<T>, MutableSharedFlow<T> by targetFlow


/**
 * 次方法会创建一个 [Int.MAX_VALUE] 容量的空间
 * 不能一直一直的处于增加的状态, 不然最终内存会崩, 只是一个缓存的作用
 */
fun <T> NormalMutableSharedFlow(
    extraBufferCapacity: Int = Int.MAX_VALUE,
): NormalMutableSharedFlow<T> = NormalMutableSharedFlowImpl(
    targetFlow = MutableSharedFlow(
        replay = 0,
        extraBufferCapacity = extraBufferCapacity,
        onBufferOverflow = BufferOverflow.SUSPEND,
    )
)

private fun <T> Flow<T>.doSharedIn(
    shareFlow: MutableSharedFlow<T>,
    scope: CoroutineScope,
    enableLog: Boolean = false,
) {
    val upstream = this
    scope.launch {
        if (enableLog) {
            Log.d(
                TAG, "开始订阅上游的流",
            )
        }
        upstream.collect {
            if (enableLog) {
                Log.d(
                    TAG,
                    "准备发射到 shareFlow, 订阅者个数为： ${shareFlow.subscriptionCount.value}, 收集到的上游的信号：$it",
                )
            }
            shareFlow.emit(value = it)
            if (enableLog) {
                Log.d(
                    TAG, "发射成功, shareFlow 收集到的上游的信号：$it",
                )
            }
        }
    }
}

fun <T> Flow<T>.mutableSharedIn(
    scope: CoroutineScope,
    enableLog: Boolean = false,
): MutableSharedFlow<T> {
    val shareFlow = MutableSharedFlow<T>()
    this.doSharedIn(
        shareFlow = shareFlow,
        scope = scope,
        enableLog = enableLog,
    )
    return shareFlow
}

fun <T> Flow<T>.sharedIn(
    scope: CoroutineScope,
    enableLog: Boolean = false,
): SharedFlow<T> {
    return this.mutableSharedIn(
        scope = scope,
        enableLog = enableLog,
    )
}