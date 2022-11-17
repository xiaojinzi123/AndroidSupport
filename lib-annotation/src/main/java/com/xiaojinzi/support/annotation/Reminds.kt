package com.xiaojinzi.support.annotation

/**
 * 表示可能不能返回结果了.
 */
@Target(
    AnnotationTarget.FUNCTION
)
@Retention(value = AnnotationRetention.SOURCE)
annotation class MaybeCannotReturn {
}

/**
 * 表示需要优化
 */
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.TYPE,
    AnnotationTarget.CLASS,
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
)
@Retention(value = AnnotationRetention.SOURCE)
annotation class NeedToOptimize(

    /**
     * 优化的原因
     */
    val value: String = "",

)