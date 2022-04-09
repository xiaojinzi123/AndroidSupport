package com.xiaojinzi.support.compose.util

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip

fun Modifier.circleClip(): Modifier {
    return this.clip(shape = CircleShape)
}