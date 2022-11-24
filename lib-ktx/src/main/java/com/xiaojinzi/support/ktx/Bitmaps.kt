package com.xiaojinzi.support.ktx

import android.graphics.Bitmap
import java.io.File

/**
 * 可以安全使用的 Bitmap
 */
inline fun <R> Bitmap.use(block: (Bitmap) -> R): R {
    try {
        return block(this)
    } finally {
        try {
            recycle()
        } catch (e: Exception) {
            // ignore
        }
    }
}

@Throws(RuntimeException::class)
fun Bitmap.writeToFile(targetFile: File): File {
    targetFile.outputStream().use { outputStream ->
        this.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
    }
    return targetFile
}