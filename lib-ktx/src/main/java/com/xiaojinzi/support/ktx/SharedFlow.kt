package com.xiaojinzi.support.ktx

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@Suppress("FunctionName", "UNCHECKED_CAST")
fun <T> NormalMutableSharedFlow() = MutableSharedFlow<T>(
    replay = 0,
    extraBufferCapacity = 1,
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