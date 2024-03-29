package com.xiaojinzi.support.ktx

import com.xiaojinzi.support.annotation.SecondTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

/**
 * 每个实现这个接口的都是一个场景
 * 实现这个接口一定要实现 [hashCode] 方法
 */
interface MemoryCacheKey

data class MemoryCacheConfig<CacheKey : MemoryCacheKey>(
    @SecondTime
    val cacheTime: Long,
    val updateCallable: suspend (CacheKey) -> Any?,
)

/**
 * 内存缓存的支持
 */
object MemoryCache {

    private const val TAG = "MemoryCache"

    private val dataMap = ConcurrentHashMap<String, Pair<Long, Any?>>()

    private val configMap = ConcurrentHashMap<Int, MemoryCacheConfig<out MemoryCacheKey>>()

    private val flow = MutableSharedStateFlow(
        initValue = dataMap,
    )

    fun <KeyType : MemoryCacheKey> setCacheConfig(
        keyClass: KClass<KeyType>,
        config: MemoryCacheConfig<KeyType>,
    ) {
        configMap[keyClass.hashCode()] = config
    }

    @Suppress("UNCHECKED_CAST")
    private fun <Key : MemoryCacheKey> checkExpired(key: Key) {
        val configKey = key::class.hashCode()
        val dataKey = "${configKey}@${key.hashCode()}"
        val targetConfig: MemoryCacheConfig<Key> =
            (configMap[configKey]
                ?: notSupportError(message = "not found config for key: $key")) as MemoryCacheConfig<Key>
        val expiredTime = dataMap[dataKey]?.first ?: 0
        // 如果过期, 执行一次更新
        if (expiredTime <= System.currentTimeMillis()) {
            // 如果过期了
            LogSupport.d(
                tag = TAG,
                content = "key $key is expired, update it, configKey = $configKey, dataKey = $dataKey",
            )
            executeTaskInCoroutinesIgnoreError {
                dataMap[dataKey] =
                    (System.currentTimeMillis() +
                            targetConfig.cacheTime) to targetConfig
                        .updateCallable(key)
                flow.emit(
                    value = dataMap,
                )
            }
        }
    }

    /**
     * 尝试刷新
     */
    fun <Key : MemoryCacheKey> tryRefresh(key: Key) {
        checkExpired(
            key = key,
        )
    }

    /**
     * 获取当前时间的数据, 过期了会触发更新
     */
    @Suppress("UNCHECKED_CAST")
    fun <T, Key : MemoryCacheKey> get(key: Key): T? {
        val configKey = key::class.hashCode()
        val dataKey = "${configKey}@${key.hashCode()}"
        checkExpired(key = key)
        return dataMap[dataKey]?.second as? T
    }

    /**
     * 订阅数据
     */
    @Suppress("UNCHECKED_CAST")
    fun <T, Key : MemoryCacheKey> subscribe(key: Key): Flow<T?> {
        val configKey = key::class.hashCode()
        val dataKey = "${configKey}@${key.hashCode()}"
        checkExpired(key = key)
        return flow.map { map ->
            kotlin.runCatching {
                map[dataKey]?.second as? T
            }.getOrNull()
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T, Key : MemoryCacheKey> subscribeValues(keyClass: KClass<Key>): Flow<List<T?>> {
        val configKey = keyClass.hashCode()
        return flow.map { map ->
            map.filter {
                it.key.startsWith(
                    prefix = "$configKey@",
                )
            }.map {
                it.value.second as? T
            }
        }
    }

}