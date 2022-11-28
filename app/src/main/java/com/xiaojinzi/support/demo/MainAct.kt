package com.xiaojinzi.support.demo

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.xiaojinzi.support.architecture.mvvm1.BaseAct
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

            }

        }

    }

}