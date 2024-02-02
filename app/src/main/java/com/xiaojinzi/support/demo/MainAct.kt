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
import com.xiaojinzi.support.ktx.SharedStartMode
import com.xiaojinzi.support.ktx.launchWhenEvent
import com.xiaojinzi.support.ktx.nothing
import com.xiaojinzi.support.ktx.sharedStateIn
import com.xiaojinzi.support.ktx.tickerFlow
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

@ActivityFlag(
    value = ["test", "test1"],
)
@UnCheckInit
class MainAct : BaseAct<MainViewModel>() {

    private val flow1 = tickerFlow(period = 1000)
        .map { System.currentTimeMillis() }

    private val flow2 = flow1
        .onEach {
            println("======= 收到 Flow1 的数据: $it")
        }
        .sharedStateIn(
            scope = lifecycleScope,
            initValue = -1L,
            sharedStartMode = SharedStartMode.WhileSubscribed,
        )

    private var job2: Job? = null

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
                        job2?.cancel()
                        job2 = flow2
                            .onEach {
                                println("======= 收到 Flow2 的数据: $it")
                            }
                            .launchIn(scope = lifecycleScope)
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
                        job2?.cancel()
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