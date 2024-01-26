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
import com.xiaojinzi.support.ktx.CacheSharedStateFlow
import com.xiaojinzi.support.ktx.launchWhenEvent
import com.xiaojinzi.support.ktx.nothing
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@ActivityFlag(
    value = ["test", "test1"],
)
@UnCheckInit
class MainAct : BaseAct<MainViewModel>() {

    private val cacheFlow = CacheSharedStateFlow(
        initValue = 0,
    )

    override fun getViewModelClass(): Class<MainViewModel> {
        return MainViewModel::class.java
    }

    private var job1: Job? = null
    private fun test1() {
        job1?.cancel()
        job1 = cacheFlow
            .onEach {
                delay(1000)
                println("result2 = $it")
            }
            .launchIn(scope = lifecycleScope)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        cacheFlow
            .onEach {
                delay(500)
                println("result1 = $it")
            }
            .launchIn(scope = lifecycleScope)

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
                        (1..10).forEach {
                            cacheFlow.add(
                                value = it
                            )
                        }
                    },
                ) {
                    Text(
                        text = "点击",
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
                        test1()
                    },
                ) {
                    Text(
                        text = "点击",
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