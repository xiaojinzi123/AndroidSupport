package com.xiaojinzi.support.ktx

import android.os.Build
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi

/**
 * View 设置隐藏
 */
inline fun View.gone() {
    this.visibility = View.GONE
}

/**
 * View 设置隐藏
 */
inline fun View.inVisible() {
    this.visibility = View.INVISIBLE
}

/**
 * View 设置显示
 */
inline fun View.visible() {
    this.visibility = View.VISIBLE
}

/**
 * 根据 transitionName 寻找控件
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
fun View.findByTransitionName(transitionName: String): View? {
    if (transitionName == this.transitionName) {
        return this
    }
    if (this is ViewGroup) {
        for (index in 0 until this.childCount) {
            val target = this.getChildAt(index)
                .findByTransitionName(transitionName)
            if (target != null) {
                return target
            }
        }
    }
    return null
}