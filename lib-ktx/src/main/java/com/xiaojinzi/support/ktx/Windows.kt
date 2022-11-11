package com.xiaojinzi.support.ktx

import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowManager

fun Window.translateStatusBar(isDark: Boolean = false) {
    //设置通知栏透明，导航栏不透明
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        this.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        this.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        this.statusBarColor = 0x00000000 // transparent
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        val flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
        this.addFlags(flags)
    }
    if (isDark) {
        this.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    } else {
        this.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }
}