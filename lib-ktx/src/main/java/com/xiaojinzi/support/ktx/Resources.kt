package com.xiaojinzi.support.ktx

import android.content.res.Resources
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat

/**
 * 状态栏高度
 */
val statusHeight: Int
    get() {
        var result = 0
        try {
            val resources: Resources = app.resources
            val resourceId: Int = resources.getIdentifier("status_bar_height", "dimen", "android")
            return resources.getDimensionPixelSize(resourceId)
        } catch (_: Exception) {
            // ignore
        }
        return result
    }

/**
 * 屏幕宽度
 */
val screenWidth = app.resources.displayMetrics.widthPixels

/**
 * 屏幕高度
 */
val screenHeight = app.resources.displayMetrics.heightPixels

/**
 * 根据 id 获取系统字符串
 */
inline fun @receiver:androidx.annotation.StringRes Int.toResString(): String =
    app.resources.getString(this)

/**
 * 根据 id 获取颜色
 */
@androidx.annotation.ColorInt
inline fun @receiver:androidx.annotation.ColorRes Int.toResColor(): Int =
    ContextCompat.getColor(app, this)

/**
 * 根据 id 获取 Drawable
 */
inline fun @receiver:androidx.annotation.DrawableRes Int.toResDrawable(): Drawable? =
    ContextCompat.getDrawable(app, this)

/**
 * 根据 id 获取 Int
 */
inline fun @receiver:androidx.annotation.IntegerRes Int.toResInt(): Int =
    app.resources.getInteger(this)

/**
 * 根据 id 获取 Bool
 */
inline fun @receiver:androidx.annotation.BoolRes Int.toResBool(): Boolean =
    app.resources.getBoolean(this)

/**
 * Int 的 dp 属性, dp to px
 */
val Int.dp
    get() = this.toFloat().dpInt

/**
 * Float 的 dp 属性, dp to px
 */
val Float.dp
    get() = app.resources.displayMetrics.density * this + 0.5f

/**
 * Float 的 dp 属性, dp to px
 */
val Float.dpInt
    get() = (this.dp + .5f).toInt() // 四舍五入