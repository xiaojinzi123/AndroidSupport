package com.xiaojinzi.support.demo

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xiaojinzi.support.compose.TextFieldCopy
import com.xiaojinzi.support.ktx.format2f
import com.xiaojinzi.support.ktx.nothing
import com.xiaojinzi.support.util.ActivityFlag
import com.xiaojinzi.support.util.LogSupport

@ActivityFlag(
    value = ["test", "test1"],
)
class MainAct : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            testContent()
        }

        LogSupport.d(content = "testData2 = ${123.0f.format2f()}")
        LogSupport.d(content = "testData3 = ${123.0.format2f()}")

    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun testContent() {
    var content by remember {
        mutableStateOf(value = "")
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .nothing(),
    ) {
        TextFieldCopy(
            modifier = Modifier
                .fillMaxWidth()
                .nothing(),
            value = content,
            onValueChange = { content = it },
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        )
        Spacer(modifier = Modifier.height(height = 10.dp).nothing())
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .nothing(),
            value = content,
            onValueChange = { content = it },
        )
    }
}