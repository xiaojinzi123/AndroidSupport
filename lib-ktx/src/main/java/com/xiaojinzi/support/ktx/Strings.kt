package com.xiaojinzi.support.ktx

import java.security.MessageDigest
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

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

/**
 * 不保函扩展名的名称
 */
val String.nameWithoutExtension: String
    get() {
        val index = this.lastIndexOf(".")
        return if (index == -1) {
            this
        } else {
            this.substring(0, index)
        }
    }

/**
 * 扩展名
 */
val String.extension: String
    get() {
        val index = this.lastIndexOf(".")
        return if (index == -1) {
            ""
        } else {
            this.substring(index + 1)
        }
    }

inline fun String?.ifNotNullOrEmpty(block: (String) -> Unit) {
    if (!this.isNullOrEmpty()) {
        block(this)
    }
}

/**
 * String 的扩展, 根据泛型的类型, 将 String 解析为对应的类型
 */
inline fun <reified T> String.toT(): T? {
    return when (T::class) {
        String::class -> this
        Int::class -> this.toIntOrNull()
        Long::class -> this.toLongOrNull()
        Float::class -> this.toFloatOrNull()
        Double::class -> this.toDoubleOrNull()
        Boolean::class -> this.toBoolean()
        else -> null
    } as T?
}