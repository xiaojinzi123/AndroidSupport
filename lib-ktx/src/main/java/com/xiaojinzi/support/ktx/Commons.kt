package com.xiaojinzi.support.ktx

import android.app.Application
import android.view.View
import com.xiaojinzi.support.init.AppInstance
import java.util.*

/**
 * 全局可用的 Application
 */
val app: Application
    get() = AppInstance.getApp()

/**
 * 创建一个 UUID
 */
fun newUUid() = UUID.randomUUID().toString()

inline fun Boolean.toVisible(isGone: Boolean = false) =
    if (this) View.VISIBLE else (if (isGone) View.GONE else View.INVISIBLE)

/**
 * 为了有些时候的链式操作, 最后的逗号用此方法搞一哈
 */
inline fun <T> T.nothing(): T {
    return this
}