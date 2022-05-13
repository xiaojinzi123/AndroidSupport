package com.xiaojinzi.support.ktx

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

/**
 * 执行这个任务最低需要的时间
 */
suspend fun <T> timeAtLeast(timeMillis: Long = 500L, callable: suspend () -> T): T {
    return withContext(context = Dispatchers.IO) {
        val job1 = async {
            delay(timeMillis = timeMillis)
        }
        val job2 = async {
            try {
                callable.invoke()
            } catch (e: Exception) {
                throw e
            } finally {
                job1.await()
            }
        }
        job2.await()
    }
}