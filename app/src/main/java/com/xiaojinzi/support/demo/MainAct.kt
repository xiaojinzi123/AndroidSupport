package com.xiaojinzi.support.demo

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.xiaojinzi.support.architecture.mvvm1.BaseAct
import com.xiaojinzi.support.init.UnCheckInit
import com.xiaojinzi.support.ktx.ActivityFlag
import com.xiaojinzi.support.ktx.LogSupport
import com.xiaojinzi.support.ktx.MemoryCache
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@ActivityFlag(
    value = ["test", "test1"],
)
@UnCheckInit
class MainAct : BaseAct<MainViewModel>() {

    private var job: Job? = null

    override fun getViewModelClass(): Class<MainViewModel> {
        return MainViewModel::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Button(onClick = {
                job?.cancel()
                job = MemoryCache
                    .subscribe<Long>(
                        key = "test",
                    )
                    .onEach {
                        LogSupport.d(
                            tag = "------",
                            content = "value = $it"
                        )
                    }
                    .launchIn(scope = lifecycleScope)
            }) {
                Text(
                    text = "点击",
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Normal,
                    ),
                    textAlign = TextAlign.Start,
                )
            }

        }

    }

}