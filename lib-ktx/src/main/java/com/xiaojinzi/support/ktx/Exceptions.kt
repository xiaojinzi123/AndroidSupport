package com.xiaojinzi.support.ktx

/**
 * 获取真实的错误
 */
fun Throwable.getRealThrowable(throwable: Throwable): Throwable {
    var throwable = throwable
    while (throwable.cause != null) {
        throwable = throwable.cause!!
    }
    return throwable
}