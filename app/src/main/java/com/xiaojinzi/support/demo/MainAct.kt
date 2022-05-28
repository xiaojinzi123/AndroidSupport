package com.xiaojinzi.support.demo

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.xiaojinzi.support.compose.GridView
import com.xiaojinzi.support.compose.TextFieldCopy
import com.xiaojinzi.support.ktx.notSupportError
import com.xiaojinzi.support.ktx.nothing
import com.xiaojinzi.support.util.ActivityFlag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

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