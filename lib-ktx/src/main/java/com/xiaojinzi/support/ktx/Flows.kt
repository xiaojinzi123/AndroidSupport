package com.xiaojinzi.support.ktx

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@ExperimentalTime
fun tickerFlow(period: Duration, initialDelay: Duration = Duration.ZERO) = flow {
    delay(initialDelay)
    while (true) {
        emit(Unit)
        delay(period)
    }
}

@FlowPreview
fun <T> Flow<T>.takeUntil(
    isIncludeLastOne: Boolean = false,
    condition: (value: T) -> Boolean
): Flow<T> {
    return FlowTakeUntil(
        source = this,
        condition = condition,
        isIncludeLastOne = isIncludeLastOne,
    )
}

@FlowPreview
internal class FlowTakeUntil<T>(
    private val source: Flow<T>,
    private val condition: (value: T) -> Boolean,
    private val isIncludeLastOne: Boolean,
) : AbstractFlow<T>() {

    @InternalCoroutinesApi
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