package com.xiaojinzi.support.ktx

import com.google.gson.Gson

fun Any.toJson(): String {
    return Gson().toJson(this)
}