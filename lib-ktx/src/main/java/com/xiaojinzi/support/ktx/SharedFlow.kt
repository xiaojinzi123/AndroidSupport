package com.xiaojinzi.support.ktx

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

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
) {
    val upstream = this
    scope.launch {
        upstream.collect {
            shareFlow.emit(value = it)
        }
    }
}

fun <T> Flow<T>.mutableSharedIn(
    scope: CoroutineScope,
): MutableSharedFlow<T> {
    val shareFlow = MutableSharedFlow<T>()
    this.doSharedIn(
        shareFlow = shareFlow,
        scope = scope,
    )
    return shareFlow
}

fun <T> Flow<T>.sharedIn(
    scope: CoroutineScope,
): SharedFlow<T> {
    return this.mutableSharedIn(
        scope = scope,
    )
}