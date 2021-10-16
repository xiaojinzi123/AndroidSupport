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
annotation class ViewLayer

@Retention(RetentionPolicy.SOURCE)
@Target(
    AnnotationTarget.TYPE_PARAMETER,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.LOCAL_VARIABLE,
    AnnotationTarget.CLASS,
    AnnotationTarget.TYPE,
)
annotation class ViewModelLayer

@Retention(RetentionPolicy.SOURCE)
@Target(
    AnnotationTarget.TYPE_PARAMETER,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.LOCAL_VARIABLE,
    AnnotationTarget.CLASS,
    AnnotationTarget.TYPE,
)
annotation class ModelLayer