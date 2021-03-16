package com.xiaojinzi.support.annotation

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * 表示一个资源是一个 "Hot Observable", 可以是 RxJava 的 [io.reactivex.subjects.Subject].
 * 也可以是 Google 官方的 [LiveData].
 * 主要起到一个标志作用
 */
@Retention(RetentionPolicy.SOURCE)
@Target(
    AnnotationTarget.ANNOTATION_CLASS,
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
    val value: Pattern
) {
    enum class Pattern {
        /**
         * 发射最近一个信号, 此模式和
         * LiveData 和 RxJava 的 BehaviorSubject 行为一致
         */
        BEHAVIOR,

        /**
         * 发射信号给订阅者在订阅者订阅之后,
         * 订阅之前的信号将丢失.
         * 类似 EventBus 和 RxJava 的 PublishSubject 的行为
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