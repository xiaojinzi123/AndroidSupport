package com.xiaojinzi.support.ktx

import com.xiaojinzi.support.init.AppInstance
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

interface DeferredEmpty : Deferred<Nothing>

/**
 * 忽略错误的一个协程的 Context
 */
val ErrorIgnoreContext: CoroutineExceptionHandler
    get() = CoroutineExceptionHandler { _, throwable ->
        if (AppInstance.isDebug) {
            throwable.printStackTrace()
        }
    }

suspend fun DeferredEmpty.awaitIgnoreException() {
    try {
        this.await()
    } catch (e: Exception) {
        // ignore
    }
}

fun DeferredEmpty.executeIgnoreException() {
    val targetDeferred = this
    AppScope.launch(context = ErrorIgnoreContext) {
        targetDeferred.awaitIgnoreException()
    }
}

/**
 * join 一个 Job, 忽略异常
 */
@Deprecated(
    message = "this method is not working, please use awaitIgnoreException()",
    replaceWith = ReplaceWith(
        expression = "awaitIgnoreException()",
        imports = ["com.xiaojinzi.support.ktx.awaitIgnoreException"]
    ),
    level = DeprecationLevel.WARNING,
)
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