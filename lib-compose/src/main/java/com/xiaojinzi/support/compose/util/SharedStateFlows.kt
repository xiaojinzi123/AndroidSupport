package com.xiaojinzi.support.compose.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import com.xiaojinzi.support.ktx.SharedStateFlow
import com.xiaojinzi.support.ktx.notSupportError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@Composable
fun <T> SharedStateFlow<T>.collectAsState(
    context: CoroutineContext = EmptyCoroutineContext,
): State<T> = collectAsState(
    initial = value,
    context = context,
)

@Composable
@Suppress("StateFlowValueCalledInComposition")
fun <T> Flow<T>.collectAsState(
    context: CoroutineContext = EmptyCoroutineContext,
): State<T> = collectAsState(
    initial = when (this) {
        is StateFlow -> this.value
        is SharedStateFlow -> this.value
        else -> notSupportError()
    },
    context = context,
)