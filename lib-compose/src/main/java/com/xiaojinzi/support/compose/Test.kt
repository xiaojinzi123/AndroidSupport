package com.xiaojinzi.support.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xiaojinzi.support.compose.TabRowDefaultsCopy3.tabIndicatorOffset
import com.xiaojinzi.support.compose.util.circleClip
import com.xiaojinzi.support.ktx.nothing

@Composable
fun Test() {

    ScrollableTabRowCopy3(
        modifier = Modifier
            .padding(top = 10.dp)
            .nothing(),
        indicator = { tabPositions ->
            Box(
                Modifier
                    .tabIndicatorOffset(tabPositions[0])
                    .width(width = 10.dp)
                    .requiredWidth(width = 10.dp)
                    .height(3.dp)
                    .circleClip()
                    .background(color = Color.Red)
                    .nothing()
            )
        },
        edgePadding = 0.dp,
        selectedTabIndex = 0,
        containerColor = Color.Transparent,
        divider = {},
    ) {

        (0..3).forEachIndexed { index, itemDto ->
            val isSelected = index == 0
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Tab(
                    modifier = Modifier
                        .nothing(),
                    selected = isSelected,
                    onClick = {
                    },
                ) {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 10.dp, vertical = 8.dp)
                            .nothing(),
                        text = "xxx",
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = Color.Black,
                            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                        ),
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }

    }

}