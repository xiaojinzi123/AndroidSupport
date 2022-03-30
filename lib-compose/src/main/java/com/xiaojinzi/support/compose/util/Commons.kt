package com.xiaojinzi.support.compose.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.xiaojinzi.support.bean.StringItemDTO

@Composable
fun StringItemDTO.contentWithComposable(): String {
    return value ?: stringResource(id = valueRsd!!)
}