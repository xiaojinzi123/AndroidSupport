package com.xiaojinzi.support.ktx

import com.xiaojinzi.support.annotation.TimeValue
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

@FlowPreview
internal class FlowTakeUntilImpl<T>(
    private val source: Flow<T>,
    private val condition: (value: T) -> Boolean,
    private val isIncludeLastOne: Boolean,
) : AbstractFlow<T>() {

    override suspend fun collectSafely(collector: FlowCollector<T>) {
        coroutineScope {
            try {
                source.collect {
                    if (condition.invoke(it)) {
                        if (isIncludeLastOne) {
                            collector.emit(value = it)
                        }
                        throw STOP
                    }
                    collector.emit(value = it)
                }
            } catch (e: StopException) {
                // ok
            }
        }
    }

    class StopException : CancellationException()

    companion object {
        val STOP = StopException()
    }

}

/**
 * 一直取, 直到条件不成立
 */
@FlowPreview
fun <T> Flow<T>.takeUntil(
    isIncludeLastOne: Boolean = false,
    condition: (value: T) -> Boolean
): Flow<T> {
    return FlowTakeUntilImpl(
        source = this,
        condition = condition,
        isIncludeLastOne = isIncludeLastOne,
    )
}

/**
 * 定时器
 */
fun tickerFlow(
    @TimeValue(value = TimeValue.Type.MILLISECOND) period: Long,
    @TimeValue(value = TimeValue.Type.MILLISECOND) initialDelay: Long = 0L
) = flow {
    delay(initialDelay)
    while (true) {
        emit(Unit)
        delay(period)
    }
}
