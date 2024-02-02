package com.xiaojinzi.support.demo

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.xiaojinzi.support.architecture.mvvm1.BaseAct
import com.xiaojinzi.support.init.UnCheckInit
import com.xiaojinzi.support.ktx.ActivityFlag
import com.xiaojinzi.support.ktx.launchWhenEvent
import com.xiaojinzi.support.ktx.nothing
import kotlinx.coroutines.delay

@ActivityFlag(
    value = ["test", "test1"],
)
@UnCheckInit
class MainAct : BaseAct<MainViewModel>() {

    override fun getViewModelClass(): Class<MainViewModel> {
        return MainViewModel::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .nothing(),
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
            ) {
                Button(
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 0.dp)
                        .fillMaxWidth()
                        .nothing(),
                    onClick = {
                    },
                ) {
                    Text(
                        text = "订阅 flow2",
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Normal,
                        ),
                        textAlign = TextAlign.Start,
                    )
                }
                Button(
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 0.dp)
                        .fillMaxWidth()
                        .nothing(),
                    onClick = {
                    },
                ) {
                    Text(
                        text = "取消订阅 flow2",
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Normal,
                        ),
                        textAlign = TextAlign.Start,
                    )
                }
            }
        }

        lifecycle.launchWhenEvent(
            event = Lifecycle.Event.ON_STOP,
        ) {
            println("xxxxxxx ON_STOP ${this@MainAct.hashCode()} ${lifecycleScope.hashCode()}")
            delay(1000)
        }

    }

}