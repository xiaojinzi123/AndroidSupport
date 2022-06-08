package com.xiaojinzi.support.util

import com.xiaojinzi.support.ktx.NormalMutableSharedFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

/**
 * 事件的载体
 */
sealed class EventDto

/**
 * 全局事件分发
 */
object EventPublisher {

    /**
     * 事件流
     */
    val eventObservable: MutableSharedFlow<EventDto> = NormalMutableSharedFlow()

}