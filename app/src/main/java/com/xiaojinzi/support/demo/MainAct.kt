package com.xiaojinzi.support.demo

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.xiaojinzi.support.compose.GridView
import com.xiaojinzi.support.ktx.nothing
import com.xiaojinzi.support.util.ActivityFlag

@ActivityFlag(
    value = ["test", "test1"],
)
class MainAct : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            testContent()
        }

    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun testContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .nothing(),
    ) {

        GridView(
            items = (0..5).toList(),
            columnNumber = 4,
            horizontalSpace = 4.dp,
            verticalSpace = 4.dp,
            contentAlignment = Alignment.TopCenter,
            contentCombineItem = { index, item ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .requiredHeight(height = if (index == 0) 100.dp else 50.dp)
                        .background(color = Color.DarkGray)
                        .nothing(),
                )
            }
        )
    }
}