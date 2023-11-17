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
import com.xiaojinzi.support.ktx.MutableSharedStateFlow
import com.xiaojinzi.support.ktx.app
import com.xiaojinzi.support.ktx.filePersistence
import com.xiaojinzi.support.ktx.times
import java.io.File

@ActivityFlag(
    value = ["test", "test1"],
)
@UnCheckInit
class MainAct : BaseAct<MainViewModel>() {

    /*private val ttt = MutableSharedStateFlow<List<Int>>()
        .filePersistence(
            scope = lifecycleScope,
            file = File(app.cacheDir, "xxx.txt"),
            def = listOf(1, 2, 3),
        )*/

    private val ttt = MutableSharedStateFlow<List<String>>()
        .filePersistence(
            scope = lifecycleScope,
            file = File(app.cacheDir, "xxx.txt"),
            def = listOf(1, 2, 3).map { it.toString() },
        )

    override fun getViewModelClass(): Class<MainViewModel> {
        return MainViewModel::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Button(onClick = {
                ttt.tryEmit(
                    value = ttt.value.times(2)
                )
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