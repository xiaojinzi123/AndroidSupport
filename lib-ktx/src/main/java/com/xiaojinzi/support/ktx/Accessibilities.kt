package com.xiaojinzi.support.ktx

import android.view.accessibility.AccessibilityNodeInfo

fun AccessibilityNodeInfo.getRootParent(): AccessibilityNodeInfo {
    var temp = this
    while (temp.parent != null) {
        temp = temp.parent
    }
    return temp
}

@Throws(RuntimeException::class)
fun AccessibilityNodeInfo.toTreeString(
    sb: StringBuffer,
    tabCount: Int = 0
) {
    sb.append("\n")
    repeat(times = tabCount) {
        sb.append("------")
    }
    sb.append("className = ${this.className}, text = ${this.text}")
    for (index in 0 until this.childCount) {
        getChild(index).toTreeString(
            sb = sb,
            tabCount = tabCount + 1
        )
    }
}