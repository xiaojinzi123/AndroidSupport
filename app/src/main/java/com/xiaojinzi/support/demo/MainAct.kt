package com.xiaojinzi.support.demo

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.xiaojinzi.support.architecture.mvvm1.BaseAct
import com.xiaojinzi.support.compose.util.circleClip
import com.xiaojinzi.support.compose.util.clickScaleEffect
import com.xiaojinzi.support.ktx.ActivityFlag
import com.xiaojinzi.support.ktx.SystemAlbum
import com.xiaojinzi.support.ktx.app
import com.xiaojinzi.support.ktx.nothing
import kotlinx.coroutines.launch
import java.io.File

@ActivityFlag(
    value = ["test", "test1"],
)
class MainAct : BaseAct<MainViewModel>() {

    override fun getViewModelClass(): Class<MainViewModel> {
        return MainViewModel::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val imageFile = File(
            app.cacheDir, "test.png"
        )

        imageFile.outputStream().use { outStream ->
            app.resources.openRawResource(R.raw.test).use { inStream ->
                inStream.copyTo(out = outStream)
            }
        }

        setContent {

            val scope = rememberCoroutineScope()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .nothing(),
            ) {

                Button(onClick = {
                    scope.launch {
                        SystemAlbum.saveImageToAlbum(
                            localFile = imageFile,
                            mimeType = SystemAlbum.MIME_TYPE_PNG,
                        )
                    }
                }) {
                    Text(
                        text = "保存图片到相册",
                    )
                }

                Text(
                    modifier = Modifier
                        .padding(horizontal = 28.dp, vertical = 0.dp)
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .circleClip()
                        .background(Color.Red)
                        .clickable {
                        }
                        .clickScaleEffect()
                        .padding(horizontal = 0.dp, vertical = 12.dp)
                        .nothing(),
                    text = "立即更新",
                    style = MaterialTheme.typography.button.copy(
                        color = Color.White,
                    ),
                    textAlign = TextAlign.Center,
                )

            }

        }

    }

}