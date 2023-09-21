package com.xiaojinzi.support.ktx

import com.xiaojinzi.support.annotation.MillisecondTime
import com.xiaojinzi.support.annotation.SecondTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.concurrent.ConcurrentHashMap

data class MemoryCacheConfig(
    @SecondTime
    val cacheTime: Long,
    @SecondTime
    val expiredTime: Long,
    val updateCallable: suspend () -> Any?,
)

/**
 * 内存缓存的支持
 */
object MemoryCache {

    private const val TAG = "MemoryCache"

    private val dataMap = ConcurrentHashMap<String, Any?>()

    private val configMap = ConcurrentHashMap<String, MemoryCacheConfig>()

    private val flow = MutableSharedStateFlow(
        initValue = dataMap,
    )

    private fun addCacheConfig(
        key: String,
        config: MemoryCacheConfig,
    ) {
        configMap[key] = config
    }

    fun addCacheConfig(
        key: String,
        @MillisecondTime
        cacheTime: Long,
        updateCallable: suspend () -> Any?,
    ) {
        addCacheConfig(
            key = key,
            config = MemoryCacheConfig(
                cacheTime = cacheTime,
                expiredTime = 0L,
                updateCallable = updateCallable,
            )
        )
    }

    /**
     * 订阅数据
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> subscribe(key: String): Flow<T?> {
        val targetConfig =
            configMap[key] ?: notSupportError(message = "not found config for key: $key")
        // 如果过期了
        if (targetConfig.expiredTime <= System.currentTimeMillis()) {
            LogSupport.d(
                tag = TAG,
                content = "key $key is expired, update it",
            )
            executeTaskInCoroutinesIgnoreError {
                // 进行更新
                configMap[key] = targetConfig.copy(
                    expiredTime = System.currentTimeMillis() + targetConfig.cacheTime,
                )
                dataMap[key] = targetConfig.updateCallable()
                flow.emit(
                    value = dataMap,
                )
            }
        }
        return flow.map {
            it[key] as? T
        }
    }

}