package com.xiaojinzi.support.ktx

import java.math.RoundingMode
import java.text.DecimalFormat

fun Float.format1f(
    isKeepZero: Boolean = true,
): String {
    return if(isKeepZero) {
        String.format("%.1f", this)
    } else {
        DecimalFormat("#.#").apply {
            this.roundingMode = RoundingMode.HALF_UP
        }.format(this)
    }
}

fun Float.format2f(
    isKeepZero: Boolean = true,
): String {
    return if(isKeepZero) {
        String.format("%.2f", this)
    } else {
        DecimalFormat("#.##").apply {
            this.roundingMode = RoundingMode.HALF_UP
        }.format(this)
    }
}

fun Float.format3f(
    isKeepZero: Boolean = true,
): String {
    return if(isKeepZero) {
        String.format("%.3f", this)
    } else {
        DecimalFormat("#.###").apply {
            this.roundingMode = RoundingMode.HALF_UP
        }.format(this)
    }
}

fun Double.format1f(
    isKeepZero: Boolean = true,
): String {
    return if(isKeepZero) {
        String.format("%.1f", this)
    } else {
        DecimalFormat("#.#").apply {
            this.roundingMode = RoundingMode.HALF_UP
        }.format(this)
    }
}

fun Double.format2f(
    isKeepZero: Boolean = true,
): String {
    return if(isKeepZero) {
        String.format("%.2f", this)
    } else {
        DecimalFormat("#.##").apply {
            this.roundingMode = RoundingMode.HALF_UP
        }.format(this)
    }
}

fun Double.format3f(
    isKeepZero: Boolean = true,
): String {
    return if(isKeepZero) {
        String.format("%.3f", this)
    } else {
        DecimalFormat("#.###").apply {
            this.roundingMode = RoundingMode.HALF_UP
        }.format(this)
    }
}

fun Int.toBoolean(): Boolean {
    return this != 0
}