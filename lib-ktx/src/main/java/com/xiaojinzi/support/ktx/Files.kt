package com.xiaojinzi.support.ktx

import java.io.File

/**
 * 使用完毕之后删除
 * 默认是成功使用之后删除
 */
suspend fun <T> File.deleteAfterUse(
    isDeleteAfterSuccess: Boolean = true,
    // 这里这个应该默认也要是 true, 我的感觉是这样的
    isDeleteAfterError: Boolean = false,
    action: suspend (File) -> T
): T {
    return try {
        val result = action(this)
        if (isDeleteAfterSuccess) {
            this.delete()
        }
        result
    } catch (e: Exception) {
        if (isDeleteAfterError) {
            this.delete()
        }
        throw e
    }
}