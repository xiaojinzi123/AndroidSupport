package com.xiaojinzi.support.ktx

/**
 * ByteArray转二进制字符串
 */
fun ByteArray.toHexString(): String {
    val sb = StringBuilder(size * 2)
    forEach {
        sb.append("%02X".format(it))
    }
    return sb.toString()
}