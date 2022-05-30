package com.xiaojinzi.support.ktx

fun Boolean.toOneOrZero(): Int {
    return if(this) 1 else 0
}