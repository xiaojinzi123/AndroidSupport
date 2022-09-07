package com.xiaojinzi.support.ktx

import com.xiaojinzi.support.annotation.FileSizeValue
import com.xiaojinzi.support.annotation.TimeValue
import java.text.SimpleDateFormat
import java.util.*

@FileSizeValue(value = FileSizeValue.Type.MB)
fun @receiver:FileSizeValue(value = FileSizeValue.Type.BYTE) Long.toMB(): Float {
    return this / 1024f / 1024f
}

@FileSizeValue(value = FileSizeValue.Type.GB)
fun @receiver:FileSizeValue(value = FileSizeValue.Type.BYTE) Long.toGB(): Float {
    return this.toMB() / 1024f
}

/**
 * 岁数的格式化
 */
fun @TimeValue(value = TimeValue.Type.MILLISECOND) Long.commonAgeFormat1(): Int =
    (getYearByTimeStamp(timeStamp = System.currentTimeMillis()) - getYearByTimeStamp(timeStamp = this))


fun @TimeValue(value = TimeValue.Type.MILLISECOND) Long.commonTimeFormat1(): String =
    SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(
        Date(this)
    )

fun @TimeValue(value = TimeValue.Type.MILLISECOND) Long.commonTimeFormat2(): String =
    SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(
        Date(this)
    )

fun @TimeValue(value = TimeValue.Type.MILLISECOND) Long.commonTimeFormat3(): String =
    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
        Date(this)
    )

fun @TimeValue(value = TimeValue.Type.MILLISECOND) Long.commonTimeFormat4(): String =
    SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(
        Date(this)
    )

fun @TimeValue(value = TimeValue.Type.MILLISECOND) Long.commonTimeFormat5(): String =
    SimpleDateFormat("HH:mm", Locale.getDefault()).format(
        Date(this)
    )

fun @TimeValue(value = TimeValue.Type.MILLISECOND) Long.commonTimeFormat6(): String =
    SimpleDateFormat("MM-dd HH:mm", Locale.getDefault()).format(
        Date(this)
    )

/**
 * 格式化为：65:44
 */
fun @TimeValue(value = TimeValue.Type.MILLISECOND) Long.commonDurationFormat1(): String {
    val minute = this / (60 * 1000)
    val second = this % (60 * 1000) / 1000
    return "${if (minute < 10) "0$minute" else "$minute"}:${if (second < 10) "0$second" else "$second"}"
}

/**
 * 格式化为：7分23秒
 */
fun @TimeValue(value = TimeValue.Type.MILLISECOND) Long.commonDurationFormat2(): String {
    val minute = this / (60 * 1000)
    val second = this % (60 * 1000) / 1000
    return "${minute}分${if (second < 10) "0$second" else "$second"}秒"
}