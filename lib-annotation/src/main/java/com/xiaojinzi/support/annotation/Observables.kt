package com.xiaojinzi.support.annotation


/**
 * 表示一个资源是一个 "Hot Observable", 可以是 RxJava 的 Subject.
 * 也可以是 Google 官方的 LiveData.
 * 主要起到一个标志作用
 */
@Retention(value = AnnotationRetention.SOURCE)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.FIELD,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.FUNCTION
)
annotation class HotObservable(

    /**
     * "Hot Observable" 的行为模式
     */
    val value: Pattern,

    /**
     * 是否共享
     */
    val isShared: Boolean = false

) {

    /**
     * 热信号的模式
     */
    enum class Pattern {
        /**
         * 发射最近一个信号, 此模式和
         * LiveData、Kotlin StateFlow、Kotlin ShareFlow(replay=1) 和 RxJava 的 BehaviorSubject 行为一致
         */
        BEHAVIOR,

        /**
         * 发射信号给订阅者在订阅者订阅之后,
         * 订阅之前的信号将丢失.
         * 类似 EventBus、ShareFlow(replay=0) 和 RxJava 的 PublishSubject 的行为
         */
        PUBLISH,

        /**
         * 会完整的发送全部信号给每一个订阅者
         * 类似于 RxJava 的 ReplaySubject
         */
        REPLAY,

        /**
         * 发射最后一个信号, 意味着完成状态不产生, 就不会收到信号
         * 类似于 RxJava 的 AsyncSubject
         */
        ASYNC
    }

}

@Retention(
    value = AnnotationRetention.SOURCE
)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.FIELD,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.FUNCTION
)
annotation class ColdObservable

@Retention(
    value = AnnotationRetention.SOURCE
)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.FIELD,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.FUNCTION
)
annotation class FloatRangeObservable(

    /**
     * 起始值
     */
    val from: Float,

    /**
     * 结束值
     */
    val to: Float,

    /**
     * 是否包含起始值
     */
    val fromInclusive: Boolean = true,

    /**
     * 是否包含结束值
     */
    val toInclusive: Boolean = true,

)

@Retention(
    value = AnnotationRetention.SOURCE
)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.FIELD,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.FUNCTION
)
annotation class IntRangeObservable(

    /**
     * 起始值
     */
    val from: Int,

    /**
     * 结束值
     */
    val to: Int,

    /**
     * 是否包含起始值
     */
    val fromInclusive: Boolean = true,

    /**
     * 是否包含结束值
     */
    val toInclusive: Boolean = true,

)

/**
 * 表示这是一个热的 Observable, 并且是 State 模式
 * 对应 RxJava 的 BehaviorSubject
 */
@Retention(
    value = AnnotationRetention.SOURCE
)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.FIELD,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.FUNCTION
)
annotation class StateHotObservable

/**
 * 表示这是一个热的 Observable, 并且是 Publish 模式
 * 对应 RxJava 的 PublishSubject
 */
@Retention(
    value = AnnotationRetention.SOURCE
)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.FIELD,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.FUNCTION
)
annotation class PublishHotObservable