package com.xiaojinzi.support.util

import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.support.ktx.NormalMutableSharedFlow
import kotlinx.coroutines.flow.MutableSharedFlow

/**
 * 全局事件分发
 */
object EventPublisher {

    /**
     * 事件流
     */
    @HotObservable(HotObservable.Pattern.PUBLISH, isShared = true)
    val eventObservable: MutableSharedFlow<Any> = NormalMutableSharedFlow()

}