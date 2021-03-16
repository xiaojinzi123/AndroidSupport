package com.xiaojinzi.support.ktx

/**
 * 让集合可以使用 * 号进行翻倍处理
 */
operator fun <T> Collection<T>.times(count: Int): List<T> {
    if (count < 0) {
        throw IllegalArgumentException("count can't < 0")
    }
    val result = ArrayList<T>(this.size * count)
    for (index in 0 until count) {
        result.addAll(this)
    }
    return result
}
