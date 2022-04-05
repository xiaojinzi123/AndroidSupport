package com.xiaojinzi.support.demo

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.lifecycleScope
import com.xiaojinzi.support.ktx.nothing
import com.xiaojinzi.support.util.ActivityFlag
import com.xiaojinzi.support.util.ComponentActivityStack
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@ActivityFlag(
    value = "test",
)
class MainAct : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            testContent()
        }

        lifecycleScope.launch {

            delay(2000)

            ComponentActivityStack.finishActivityWithFlag(
                flag = "tes1t"
            )

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
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .nothing(),
            onClick = {

            }
        ) {
            Text(
                text = "测试",
                style = MaterialTheme.typography.body2,
                textAlign = TextAlign.Center,
            )
        }
    }
}