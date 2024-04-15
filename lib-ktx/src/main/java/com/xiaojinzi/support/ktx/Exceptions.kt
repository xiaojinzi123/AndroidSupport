package com.xiaojinzi.support.ktx

import kotlin.reflect.KClass

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

/**
 * 会从顶端开始找是否是 [targetClass] 类型的异常, 找到就返回
 */
@Suppress("UNCHECKED_CAST")
fun <T: Throwable> Throwable.findException(targetClass: KClass<T>): T? {
    var throwable: Throwable? = this
    while (throwable != null) {
        if (targetClass.isInstance(throwable)) {
            return throwable as T
        }
        throwable = throwable.cause
    }
    return null
}