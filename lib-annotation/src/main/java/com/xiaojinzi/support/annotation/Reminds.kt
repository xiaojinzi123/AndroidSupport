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