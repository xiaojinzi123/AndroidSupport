package com.xiaojinzi.support.util

import com.xiaojinzi.support.ktx.NormalMutableSharedFlow
import kotlinx.coroutines.flow.Flow

sealed class EventDto

/**
 * 全局事件分发
 */
object Event {

    /**
     * 事件流
     */
    val eventObservable: Flow<EventDto> = NormalMutableSharedFlow()

}