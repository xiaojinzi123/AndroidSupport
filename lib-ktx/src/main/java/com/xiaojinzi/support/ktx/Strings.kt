package com.xiaojinzi.support.ktx

import java.security.MessageDigest

/**
 * MD5加密，失败返回null
 */
fun String.md5(): String? {
    return try {
        val md5 = MessageDigest.getInstance("MD5")
        val strText = toByteArray()
        md5.update(strText, 0, strText.size)
        md5.digest().toHexString()
    } catch (e: Exception) {
        null
    }
}