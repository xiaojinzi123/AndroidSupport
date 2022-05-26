package com.xiaojinzi.support.demo

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
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

        /*flowOf(1, 2, 3)
            .onEach {
                notSupportError()
            }
            .flowOn(context = Dispatchers.IO)
            .launchIn(scope = lifecycleScope)*/

    }

}

@Composable
fun testContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .nothing(),
    ) {
        Button(onClick = { /*TODO*/ }) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(horizontal = 0.dp, vertical = 8.dp)
                    .nothing(),
                text = "测试",
                style = MaterialTheme.typography.body2,
                textAlign = TextAlign.Center,
            )
        }
    }
}