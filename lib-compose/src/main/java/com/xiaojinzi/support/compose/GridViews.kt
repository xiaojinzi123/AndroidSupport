package com.xiaojinzi.support.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.xiaojinzi.support.ktx.nothing

@Composable
@ExperimentalFoundationApi
fun <T> GridView(
    modifier: Modifier = Modifier,
    items: List<T>,
    horizontalSpace: Dp = 0.dp,
    verticalSpace: Dp = 0.dp,
    columnNumber: Int,
    headerContent: @Composable (index: Int) -> Unit = {},
    afterRowContent: @Composable (index: Int) -> Unit = {},
    // 最前面一个 item 的
    firstItemContent: (@Composable () -> Unit)? = null,
    // 最后一个 Item 的
    lastItemContent: (@Composable () -> Unit)? = null,
    contentAlignment: Alignment = Alignment.Center,
    contentCombineItem: @Composable (BoxScope.(index: Int, item: T) -> Unit)? = null,
    contentItem: @Composable (BoxScope.(item: T) -> Unit)? = null,
) {
    val indexOffset = if (firstItemContent == null) 0 else -1
    val realItemSize =
        items.size + (if (firstItemContent == null) 0 else 1) + (if (lastItemContent == null) 0 else 1)
    if (realItemSize == 0) {
        return
    }
    // 计算行数
    val rows = (realItemSize + columnNumber - 1) / columnNumber
    Box(
        modifier = modifier
            .nothing(),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .nothing(),
        ) {
            // header 头
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(intrinsicSize = IntrinsicSize.Min)
                    .nothing(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                for (columnIndex in 0 until columnNumber) {
                    Box(
                        modifier = Modifier
                            // .background(color = Color.Green)
                            .weight(1f, fill = true)
                            .fillMaxHeight()
                            .nothing(),
                        contentAlignment = Alignment.Center,
                        propagateMinConstraints = false,
                    ) {
                        headerContent(columnIndex)
                    }
                }
            }
            // 真正的内容
            for (rowIndex in 0 until rows) {
                if (rowIndex > 0) {
                    Spacer(
                        modifier = Modifier
                            .height(height = verticalSpace)
                            .nothing()
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(intrinsicSize = IntrinsicSize.Min)
                        .nothing(),
                    verticalAlignment = Alignment.CenterVertically,
                    // horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    for (columnIndex in 0 until columnNumber) {
                        val itemIndex = rowIndex * columnNumber + columnIndex + indexOffset
                        if (columnIndex > 0) {
                            Spacer(
                                modifier = Modifier
                                    .width(width = horizontalSpace)
                                    .nothing()
                            )
                        }
                        if (itemIndex == -1 && firstItemContent != null) {
                            Box(
                                modifier = Modifier
                                    .weight(1f, fill = true)
                                    .fillMaxHeight()
                                    .nothing(),
                                contentAlignment = contentAlignment,
                                propagateMinConstraints = false,
                            ) {
                                firstItemContent()
                            }
                        } else if (itemIndex < items.size) {
                            Box(
                                modifier = Modifier
                                    .weight(1f, fill = true)
                                    .fillMaxHeight()
                                    .nothing(),
                                contentAlignment = contentAlignment,
                                propagateMinConstraints = false,
                            ) {
                                if (contentCombineItem != null) {
                                    contentCombineItem(itemIndex, items[itemIndex])
                                } else {
                                    if (contentItem != null) {
                                        contentItem(items[itemIndex])
                                    }
                                }
                            }
                        } else {
                            if (lastItemContent != null && itemIndex == items.size) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f, fill = true)
                                        .fillMaxHeight()
                                        .nothing(),
                                    contentAlignment = contentAlignment,
                                    propagateMinConstraints = false,
                                ) {
                                    lastItemContent()
                                }
                            } else {
                                Spacer(
                                    modifier = Modifier
                                        .weight(1f, fill = true)
                                        .nothing()
                                )
                            }
                        }
                    }
                }
                // 插入一个每行结束的行的内容
                afterRowContent(rowIndex)
            }
        }
    }
}

@ExperimentalFoundationApi
@Preview
@Composable
private fun GridViewPreview() {
    GridView(
        items = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9),
        columnNumber = 4,
        headerContent = {
            Text(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .nothing(),
                text = "标题$it",
            )
        },
        afterRowContent = {
            Box(
                modifier = Modifier
                    .background(color = Color.Red)
                    .fillMaxWidth()
                    .height(height = 20.dp)
            )
        },
        firstItemContent = {
            Text(
                text = "我是最前面一个 Item",
                style = MaterialTheme.typography.body2,
                textAlign = TextAlign.Center,
            )
        },
        lastItemContent = {
            Text(
                text = "我是最后一个 Item",
                style = MaterialTheme.typography.body2,
                textAlign = TextAlign.Center,
            )
        },
    ) {
        Text(
            modifier = Modifier
                .padding(vertical = 20.dp)
                .nothing(),
            text = "测试".repeat(n = it % 3) + "$it",
        )
    }
}