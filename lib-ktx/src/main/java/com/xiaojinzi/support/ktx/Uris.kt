package com.xiaojinzi.support.ktx

import android.net.Uri
import java.io.File

/**
 * 复制文件
 */
fun Uri.copyFileTo(destFile: File): File {
    destFile.outputStream().use { outputStream ->
        app.contentResolver.openInputStream(this)?.use { inputStream ->
            inputStream.copyTo(outputStream)
        }
    }
    return destFile
}