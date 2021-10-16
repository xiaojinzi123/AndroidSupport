package com.xiaojinzi.support.annotation

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

// 下面的三个用来标记不同的层

@Retention(RetentionPolicy.SOURCE)
@Target(
    AnnotationTarget.TYPE_PARAMETER,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.LOCAL_VARIABLE,
    AnnotationTarget.CLASS,
    AnnotationTarget.TYPE,
)
annotation class V

@Retention(RetentionPolicy.SOURCE)
@Target(
    AnnotationTarget.TYPE_PARAMETER,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.LOCAL_VARIABLE,
    AnnotationTarget.CLASS,
    AnnotationTarget.TYPE,
)
annotation class VM

@Retention(RetentionPolicy.SOURCE)
@Target(
    AnnotationTarget.TYPE_PARAMETER,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.LOCAL_VARIABLE,
    AnnotationTarget.CLASS,
    AnnotationTarget.TYPE,
)
annotation class M