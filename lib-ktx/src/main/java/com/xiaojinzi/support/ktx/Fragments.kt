package com.xiaojinzi.support.ktx

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

fun FragmentManager.replaceCommit(@IdRes containerViewId: Int, frag: Fragment) {
    val ft = this.beginTransaction()
    ft.replace(containerViewId, frag)
    ft.commit()
}

fun FragmentManager.replaceCommitNow(@IdRes containerViewId: Int, frag: Fragment) {
    val ft = this.beginTransaction()
    ft.replace(containerViewId, frag)
    ft.commitNow()
}

fun FragmentManager.replaceCommitAllowingStateLoss(@IdRes containerViewId: Int, frag: Fragment) {
    val ft = this.beginTransaction()
    ft.replace(containerViewId, frag)
    ft.commitAllowingStateLoss()
}

fun FragmentManager.replaceCommitNowAllowingStateLoss(@IdRes containerViewId: Int, frag: Fragment) {
    val ft = this.beginTransaction()
    ft.replace(containerViewId, frag)
    ft.commitNowAllowingStateLoss()
}