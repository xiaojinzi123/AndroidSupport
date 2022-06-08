package com.xiaojinzi.support.ktx

import java.io.File

/**
 * 使用完毕之后删除
 * 默认是成功使用之后删除
 */
suspend fun File.deleteAfterUse(
    isDeleteAfterSuccess: Boolean = true,
    isDeleteAfterError: Boolean = false,
    action: suspend (File) -> Unit
) {
    try {
        action(this)
        if (isDeleteAfterSuccess) {
            this.delete()
        }
    } catch (e: Exception) {
        if (isDeleteAfterError) {
            this.delete()
        }
        throw e
    }
}