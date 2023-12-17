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
     * 发布事件,
     * 也可以自己使用 eventObservable 发布事件, 调用 add 或者 tryEmit 都可以
     */
    fun publish(event: Any) {
        eventObservable.add(value = event)
    }

}