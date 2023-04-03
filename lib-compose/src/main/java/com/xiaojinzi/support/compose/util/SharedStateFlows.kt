package com.xiaojinzi.support.compose.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import com.xiaojinzi.support.ktx.SharedStateFlow
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@Composable
fun <T> SharedStateFlow<T>.collectAsState(
    context: CoroutineContext = EmptyCoroutineContext,
): State<T> = collectAsState(
    initial = value,
    context = context,
)