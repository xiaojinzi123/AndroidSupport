package com.xiaojinzi.support.annotation

/**
 * 表示一个时间
 */
@Retention(value = AnnotationRetention.SOURCE)
@Target(
    AnnotationTarget.PROPERTY,
    AnnotationTarget.FIELD,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.TYPE_PARAMETER,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.LOCAL_VARIABLE,
    AnnotationTarget.TYPE,
    AnnotationTarget.FUNCTION,
)
annotation class TimeValue(

    /**
     * 值的类型
     */
    val value: Type

) {

    enum class Type {

        /**
         * 毫秒值
         */
        MILLISECOND,

        /**
         * 秒
         */
        SECOND,

        /**
         * 分钟
         */
        MINUTE,

        /**
         * 小时
         */
        HOUR,

        /**
         * 天
         */
        DAY,

    }

}

/**
 * 表示一个文件大小的值
 */
@Retention(value = AnnotationRetention.SOURCE)
@Target(
    AnnotationTarget.PROPERTY,
    AnnotationTarget.FIELD,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.TYPE_PARAMETER,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.LOCAL_VARIABLE,
    AnnotationTarget.FUNCTION,
)
annotation class FileSizeValue(

    /**
     * 值的类型
     */
    val value: Type

) {

    enum class Type {

        /**
         * 字节
         */
        BYTE,

        /**
         * KB
         */
        KB,

        /**
         * MB
         */
        MB,

        /**
         * GB
         */
        GB,

        /**
         * TB
         */
        TB,

    }

}