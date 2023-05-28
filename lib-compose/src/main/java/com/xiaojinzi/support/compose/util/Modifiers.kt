package com.xiaojinzi.support.compose.util

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.pointerInput
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

/**
 * 点击的事件的缩放效果
 */
@Composable
fun Modifier.clickScaleEffect(minScale: Float = 0.8f): Modifier {
    var scale by remember {
        mutableStateOf(
            1f
        )
    }
    val animScale by animateFloatAsState(targetValue = scale, label = "clickScaleEffectAnim")
    return this
        .scale(scale = animScale)
        .pointerInput(Unit) {
            // 等待按下
            awaitPointerEventScope {
                while (true) {
                    awaitFirstDown()
                    scale = minScale
                    waitForUpOrCancellation()
                    scale = 1f
                }
            }
        }

}