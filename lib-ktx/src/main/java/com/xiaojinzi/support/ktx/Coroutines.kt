package com.xiaojinzi.support.ktx

import com.xiaojinzi.support.init.AppInstance
import kotlinx.coroutines.*
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * 忽略错误的一个协程的 Context
 */
val ErrorIgnoreContext: CoroutineExceptionHandler
    get() = CoroutineExceptionHandler { _, throwable ->
        if (AppInstance.isDebug) {
            throwable.printStackTrace()
        }
    }

/**
 * join 一个 Job, 忽略异常
 */
suspend fun Job.joinIgnoreException() {
    try {
        join()
    } catch (e: Exception) {
        // ignore
    }
}

fun List<Job>.cancelAll(cause: CancellationException? = null) {
    this.forEach { it.cancel(cause = cause) }
}

fun <T> Continuation<T>.resumeIgnoreException(value: T) {
    try {
        resume(value = value)
    } catch (e: Exception) {
        // ignore
    }
}

fun <T> Continuation<T>.resumeExceptionIgnoreException(exception: Throwable) {
    try {
        resumeWithException(exception = exception)
    } catch (e: Exception) {
        // ignore
    }
}

fun <T> CancellableContinuation<T>.resumeIgnoreException(value: T) {
    try {
        resume(value = value)
    } catch (e: Exception) {
        // ignore
    }
}

fun <T> CancellableContinuation<T>.resumeExceptionIgnoreException(exception: Throwable) {
    try {
        resumeWithException(exception = exception)
    } catch (e: Exception) {
        // ignore
    }
}