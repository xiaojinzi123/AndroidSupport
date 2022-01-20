package com.xiaojinzi.support.ktx

import com.xiaojinzi.support.init.AppInstance
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job

/**
 * 忽略错误的一个协程的 Context
 */
val ErrorIgnoreContext: CoroutineExceptionHandler
    get() = CoroutineExceptionHandler { _, throwable ->
        if (AppInstance.isDebug) {
            throwable.printStackTrace()
        }
    }

fun List<Job>.cancelAll(cause: CancellationException? = null) {
    this.forEach { it.cancel(cause = cause) }
}