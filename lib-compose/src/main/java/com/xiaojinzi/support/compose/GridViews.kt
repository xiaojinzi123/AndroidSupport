package com.xiaojinzi.support.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    // 最后一个 Item 的
    lastItemContent: (@Composable () -> Unit)? = null,
    contentAlignment: Alignment = Alignment.Center,
    contentCombineItem: @Composable (BoxScope.(index: Int, item: T) -> Unit)? = null,
    contentItem: @Composable (BoxScope.(item: T) -> Unit)? = null,
) {
    val realItemSize = items.size + (if (lastItemContent == null) 0 else 1)
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
                .fillMaxWidth(),
        ) {
            // header 头
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(intrinsicSize = IntrinsicSize.Min),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                for (columnIndex in 0 until columnNumber) {
                    Box(
                        modifier = Modifier
                            // .background(color = Color.Red)
                            .weight(1f, fill = true)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center,
                        propagateMinConstraints = true
                    ) {
                        headerContent(columnIndex)
                    }
                }
            }
            // 真正的内容
            for (rowIndex in 0 until rows) {
                if (rowIndex > 0) {
                    Spacer(modifier = Modifier.height(height = verticalSpace).nothing())
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(intrinsicSize = IntrinsicSize.Min),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    for (columnIndex in 0 until columnNumber) {
                        val itemIndex = rowIndex * columnNumber + columnIndex
                        if (columnIndex > 0) {
                            Spacer(modifier = Modifier.width(width = horizontalSpace).nothing())
                        }
                        if (itemIndex < items.size) {
                            Box(
                                modifier = Modifier
                                    .weight(1f, fill = true)
                                    .fillMaxHeight(),
                                contentAlignment = contentAlignment,
                                propagateMinConstraints = true
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
                                        .fillMaxHeight(),
                                    contentAlignment = contentAlignment,
                                    propagateMinConstraints = true
                                ) {
                                    lastItemContent()
                                }
                            } else {
                                Spacer(Modifier.weight(1f, fill = true))
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
                text = "标题$it"
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
        lastItemContent = {
            Text(
                text = "我是最后一个 Item",
                style = MaterialTheme.typography.body2,
            )
        }
    ) {
        Text(
            modifier = Modifier.padding(vertical = 20.dp),
            text = "测试$it"
        )
    }
}