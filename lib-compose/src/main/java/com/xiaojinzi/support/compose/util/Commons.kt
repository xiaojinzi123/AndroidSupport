package com.xiaojinzi.support.compose.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.xiaojinzi.support.bean.StringItemDto

@Composable
fun StringItemDto.contentWithComposable(): String {
    return value ?: stringResource(id = valueRsd!!)
}

@Composable
fun Float.toDp(): Dp {
    val value = this
    return with(LocalDensity.current) {
        value.toDp()
    }
}

@Composable
fun Int.toDp(): Dp {
    return this.toFloat().toDp()
}