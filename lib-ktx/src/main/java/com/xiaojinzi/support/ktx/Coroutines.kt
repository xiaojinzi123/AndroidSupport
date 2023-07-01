package com.xiaojinzi.support.ktx

import com.xiaojinzi.support.init.AppInstance
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

interface SuspendFunction0<R> {

    suspend fun invoke(): R

}

interface SuspendAction0 {

    suspend fun invoke()

}

fun suspendAction0(action: suspend () -> Unit): SuspendAction0 {
    return object : SuspendAction0 {
        override suspend fun invoke() {
            action.invoke()
        }
    }
}

fun <R> suspendFunction0(callable: suspend () -> R): SuspendFunction0<R> {
    return object : SuspendFunction0<R> {
        override suspend fun invoke(): R {
            return callable.invoke()
        }
    }
}

/**
 * 忽略错误的一个协程的 Context
 */
val ErrorIgnoreContext: CoroutineExceptionHandler
    get() = CoroutineExceptionHandler { _, throwable ->
        if (AppInstance.isDebug) {
            throwable.printStackTrace()
        }
    }

suspend fun SuspendAction0.await() {
    this.invoke()
}

suspend fun SuspendAction0.awaitIgnoreException() {
    try {
        this.invoke()
    } catch (e: Exception) {
        // ignore
    }
}

fun SuspendAction0.executeIgnoreException() {
    val targetAction = this
    AppScope.launch(context = ErrorIgnoreContext) {
        targetAction.invoke()
    }
}

suspend fun <R> SuspendFunction0<R>.await(): R {
    return this.invoke()
}

suspend fun <R> SuspendFunction0<R>.awaitOrNull(): R? {
    return try {
        this.invoke()
    } catch (e: Exception) {
        null
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