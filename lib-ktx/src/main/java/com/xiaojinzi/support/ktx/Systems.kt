package com.xiaojinzi.support.ktx

import android.app.ActivityManager
import android.content.Context
import android.os.Build
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