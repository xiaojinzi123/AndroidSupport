package com.xiaojinzi.support.annotation

/**
 * 表示数据模型
 */
@Retention(value = AnnotationRetention.SOURCE)
@Target(
    AnnotationTarget.PROPERTY,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.TYPE_PARAMETER,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.LOCAL_VARIABLE,
    AnnotationTarget.CLASS,
    AnnotationTarget.TYPE,
)
@Repeatable
annotation class Model

/**
 * 用于网络请求
 */
typealias ModelForNetwork = Model

/**
 * 表示和 Json 之间序列化的模型
 */
typealias ModelForJson = Model

/**
 * 用于逻辑处理
 */
typealias ModelDto = Model

/**
 * 用于视图层
 */
typealias ModelVo = Model

/**
 * 用于视图层的逻辑处理
 */
typealias ModelVoDto = Model