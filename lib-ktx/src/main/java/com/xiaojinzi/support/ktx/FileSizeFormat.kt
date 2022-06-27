package com.xiaojinzi.support.ktx

import com.xiaojinzi.support.annotation.FileSizeValue

@FileSizeValue(value = FileSizeValue.Type.MB)
fun @receiver:FileSizeValue(value = FileSizeValue.Type.BYTE) Long.toMB(): Float {
    return this / 1024f / 1024f
}

@FileSizeValue(value = FileSizeValue.Type.GB)
fun @receiver:FileSizeValue(value = FileSizeValue.Type.BYTE) Long.toGB(): Float {
    return this.toMB() / 1024f
}