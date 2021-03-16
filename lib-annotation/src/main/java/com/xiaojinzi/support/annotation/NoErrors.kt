package com.xiaojinzi.support.annotation

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * 表示不会有错误发生的 Observable(可观察者对象)
 */
@Retention(RetentionPolicy.SOURCE)
@Target(
    AnnotationTarget.FUNCTION
)
annotation class NoErrorObservable