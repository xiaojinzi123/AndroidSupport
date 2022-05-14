package com.xiaojinzi.support.compose.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.xiaojinzi.support.bean.StringItemDto

@Composable
fun StringItemDto.contentWithComposable(): String {
    return value ?: stringResource(id = valueRsd!!)
}