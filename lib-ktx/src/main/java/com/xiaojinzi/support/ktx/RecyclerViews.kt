package com.xiaojinzi.support.ktx

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by heartzert on 1/27/21.
 * Email: heartzert@163.com
 */

/**
 * 给 recyclerview 设置 item decoration.
 */
fun RecyclerView.addItemDecoration(left: Int, top: Int, right: Int, bottom: Int) {
    this.addItemDecoration(object : RecyclerView.ItemDecoration() {
      override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
      ) {
        outRect.top = top
        outRect.left = left
        outRect.right = right
        outRect.bottom = bottom
        super.getItemOffsets(outRect, view, parent, state)
      }
    })
}

/**
 * 给 recyclerview 设置 item decoration.
 * 上下左右边距一样
 */
fun RecyclerView.addItemDecoration(all: Int) = this.addItemDecoration(all, all, all, all)