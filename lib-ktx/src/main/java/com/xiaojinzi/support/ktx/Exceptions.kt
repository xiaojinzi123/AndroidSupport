package com.xiaojinzi.support.ktx

/**
 * 获取真实的错误
 */
val Throwable.realThrowable: Throwable
    get() {
        var throwable = this
        while (throwable.cause != null) {
            throwable = throwable.cause!!
        }
        return throwable
    }