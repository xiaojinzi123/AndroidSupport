package com.xiaojinzi.support.compose.util

import androidx.compose.ui.graphics.Color

fun String.color(): Color = Color(android.graphics.Color.parseColor(this))