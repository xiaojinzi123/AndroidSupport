package com.xiaojinzi.support.ktx

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.coroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine

fun Lifecycle.launchWhenEvent(
    event: Lifecycle.Event,
    block: suspend CoroutineScope.() -> Unit,
): Job {
    val targetEvent = event
    return coroutineScope.launch {
        suspendCancellableCoroutine { cot ->
            val observer = object : LifecycleEventObserver {
                override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                    if (event == targetEvent) {
                        this@launchWhenEvent.removeObserver(this)
                        cot.resumeIgnoreException(
                            value = Unit
                        )
                    }
                }
            }
            this@launchWhenEvent.addObserver(
                observer = observer
            )
            cot.invokeOnCancellation {
                this@launchWhenEvent.removeObserver(observer)
            }
        }
        block()
    }
}