package com.xiaojinzi.support.ktx

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow

@Suppress("FunctionName", "UNCHECKED_CAST")
fun <T> NormalMutableSharedFlow() = MutableSharedFlow<T>(
    replay = 0,
    extraBufferCapacity = 1,
    onBufferOverflow = BufferOverflow.SUSPEND,
)