package com.xiaojinzi.support.ktx

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi

/**
 * 是否在后台运行
 */
val isRunningInBackground: Boolean
    get() {
        val packageName = app.packageName
        return (app.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
            .runningAppProcesses
            .any {
                it.processName == packageName && it.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
            }
    }


/**
 * 是否可以悬浮窗
 */
val canDrawOverlays: Boolean
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        Settings.canDrawOverlays(app)
    } else {
        true
    }