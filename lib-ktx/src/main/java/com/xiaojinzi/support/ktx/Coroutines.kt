package com.xiaojinzi.support.ktx

import com.xiaojinzi.support.init.AppInstance
import kotlinx.coroutines.CoroutineExceptionHandler

/**
 * 忽略错误的一个协程的 Context
 */
val ErrorIgnoreContext: CoroutineExceptionHandler
    get() = CoroutineExceptionHandler { _, throwable ->
        if (AppInstance.isDebug) {
            throwable.printStackTrace()
        }
    }