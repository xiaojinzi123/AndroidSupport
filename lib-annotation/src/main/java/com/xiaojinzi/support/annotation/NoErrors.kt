package com.xiaojinzi.support.annotation

/**
 * 表示不会有错误发生的方法
 */
@Retention(value = AnnotationRetention.SOURCE)
@Target(
    AnnotationTarget.FUNCTION
)
annotation class NoError

/**
 * 表示不会有错误发生的 Observable(可观察者对象)
 */
@Retention(value = AnnotationRetention.SOURCE)
@Target(
    AnnotationTarget.FUNCTION
)
annotation class NoErrorObservable