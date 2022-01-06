package com.xiaojinzi.support.ktx

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