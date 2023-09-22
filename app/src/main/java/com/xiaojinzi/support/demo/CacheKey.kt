package com.xiaojinzi.support.demo

import com.xiaojinzi.support.ktx.MemoryCacheKey

data class CacheUserKey(
    val url: String
) : MemoryCacheKey