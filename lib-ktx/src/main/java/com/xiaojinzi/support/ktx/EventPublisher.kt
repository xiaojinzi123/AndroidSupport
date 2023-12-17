package com.xiaojinzi.support.ktx

import com.xiaojinzi.support.annotation.PublishHotObservable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * 全局事件分发
 */
object EventPublisher {

    /**
     * 事件流
     * 内部的缓存默认是无限的
     */
    @PublishHotObservable
    val eventObservable: NormalMutableSharedFlow<Any> = NormalMutableSharedFlow()

    /**
     * 发布事件
     */
    fun publish(event: Any) {
        eventObservable.add(value = event)
    }

    /**
     * 在某一个作用域内订阅某一个事件
     * 如果这个 Api 不够用, 请自己拿 [eventObservable] 去订阅
     */
    inline fun <reified T> CoroutineScope.subscribe(key: T, noinline onNext: suspend (T) -> Unit) {
        eventObservable
            .filterIsInstance<T>()
            .onEach(action = onNext)
            .launchIn(scope = this)
    }

}