package com.xiaojinzi.support.ktx

fun Float.format1f(): String {
    return String.format("%.1f", this)
}

fun Float.format2f(): String {
    return String.format("%.2f", this)
}

fun Float.format3f(): String {
    return String.format("%.3f", this)
}

fun Double.format1f(): String {
    return String.format("%.1f", this)
}

fun Double.format2f(): String {
    return String.format("%.2f", this)
}

fun Double.format3f(): String {
    return String.format("%.3f", this)
}

fun Int.toBoolean(): Boolean {
    return this != 0
}