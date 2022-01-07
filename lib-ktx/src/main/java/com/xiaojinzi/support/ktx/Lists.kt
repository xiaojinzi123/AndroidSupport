package com.xiaojinzi.support.ktx

/**
 * 如果为空, 就都处理成 Null
 */
fun <T> List<T>.orNull(element: T): List<T>? {
    return if (this.isNullOrEmpty()) {
        null
    } else {
        this
    }
}

/**
 * 列表的添加或者删除
 */
fun <T> List<T>.addOrRemove(element: T): List<T> {
    return if (this.contains(element)) {
        this.toMutableList().also {
            it.remove(element)
        }
    } else {
        this.toMutableList().also {
            it.add(element)
        }
    }
}