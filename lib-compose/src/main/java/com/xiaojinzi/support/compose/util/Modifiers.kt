package com.xiaojinzi.support.compose.util

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role

/**
 * 圆形裁剪
 */
fun Modifier.circleClip(): Modifier {
    return this.clip(shape = CircleShape)
}

/**
 * 没有涟漪效果的点击
 */
fun Modifier.clickableNoRipple(
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    onClick: () -> Unit,
) = composed {
    Modifier.clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() },
        enabled = enabled,
        onClickLabel = onClickLabel,
        role = role,
        onClick = onClick,
    )
}

/**
 * 点击事件占位
 */
fun Modifier.clickPlaceholder() = this.clickableNoRipple(enabled = false) {}