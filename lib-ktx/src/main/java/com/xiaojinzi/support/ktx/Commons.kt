package com.xiaojinzi.support.ktx

import android.app.Application
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