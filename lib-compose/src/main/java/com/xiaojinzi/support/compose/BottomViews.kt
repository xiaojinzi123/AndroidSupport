package com.xiaojinzi.support.compose

import androidx.annotation.FloatRange
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xiaojinzi.support.compose.util.clickableNoRipple
import com.xiaojinzi.support.ktx.nothing
import com.xiaojinzi.support.ktx.tryFinishActivity

@Composable
fun BottomView(
    @FloatRange(from = 0.0, to = 1.0) maxFraction: Float = 0.5f,
    background: Color = Color.Black.copy(
        alpha = 0.6f
    ),
    onBackgroundClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .background(
                color = background
            )
            .fillMaxWidth()
            .wrapContentHeight(),
    ) {
        val backCallback = {
            if (onBackgroundClick == null) {
                context.tryFinishActivity()
            } else {
                onBackgroundClick()
            }
        }
        // 这个占领剩余的部分
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(fraction = 1f - maxFraction)
                .clickableNoRipple {
                    backCallback.invoke()
                }
                .nothing()
        )
        Column(
            modifier = Modifier
                .weight(weight = 1f, fill = true)
                .nothing(),
        ) {
            // 这个把真正的内容挤压到最后去
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(weight = 1f, fill = true)
                    .clickableNoRipple {
                        backCallback.invoke()
                    }
                    .nothing(),
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .nothing(),
            ) {
                content()
            }
        }
    }
}

@Preview
@Composable
private fun BottomViewPreview() {
    BottomView {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(color = Color.Red)
                .nothing(),
        )
    }
}