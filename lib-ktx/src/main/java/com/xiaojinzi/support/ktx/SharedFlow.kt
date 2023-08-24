package com.xiaojinzi.support.ktx

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

private const val TAG = "SharedFlow"

/**
 * 次方法会创建一个 [Int.MAX_VALUE] 容量的空间
 * 不能一直一直的处于增加的状态, 不然最终内存会崩, 只是一个缓存的作用
 */
@Suppress("FunctionName", "UNCHECKED_CAST")
fun <T> NormalMutableSharedFlow(extraBufferCapacity: Int = Int.MAX_VALUE) = MutableSharedFlow<T>(
    replay = 0,
    extraBufferCapacity = extraBufferCapacity,
    onBufferOverflow = BufferOverflow.SUSPEND,
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
                    TAG, "准备发射到 shareFlow 收集到的上游的信号：$it",
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