package com.xiaojinzi.support.ktx

import android.graphics.Bitmap
import java.io.File

@Throws(RuntimeException::class)
fun Bitmap.writeToFile(targetFile: File): File {
    targetFile.outputStream().use { outputStream ->
        this.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
    }
    return targetFile
}