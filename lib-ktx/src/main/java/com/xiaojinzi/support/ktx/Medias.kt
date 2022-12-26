package com.xiaojinzi.support.ktx

import android.graphics.BitmapFactory
import java.io.File

/**
 * 获取图片的大小
 */
fun File.getImageSize(): Pair<Int, Int> {
    return this.inputStream().use {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeStream(
            it,
            null,
            options,
        )
        options.outWidth to options.outHeight
    }
}

/**
 * 获取图片的大小
 */
fun File.getImageSizeOrNull(): Pair<Int, Int>? {
    return try {
        getImageSize()
    } catch (e: Exception) {
        null
    }
}