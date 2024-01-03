package com.xiaojinzi.support.compose.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

private val Int.dp2sp: TextUnit
    @Composable get() = with(LocalDensity.current) {
        this@dp2sp.dp.toPx().toSp()
    }

private val Float.dp2sp: TextUnit
    @Composable get() = with(LocalDensity.current) {
        this@dp2sp.dp.toPx().toSp()
    }

private val Double.dp2sp: TextUnit
    @Composable get() = with(LocalDensity.current) {
        this@dp2sp.dp.toPx().toSp()
    }
