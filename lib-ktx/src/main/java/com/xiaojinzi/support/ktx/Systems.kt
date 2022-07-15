package com.xiaojinzi.support.ktx

import android.app.ActivityManager
import android.os.Build
import androidx.annotation.RequiresApi

/**
 * 是否在后台运行
 */
val isRunningInBackground: Boolean
    @RequiresApi(Build.VERSION_CODES.M)
    get() {
        val packageName = app.packageName
        return app
            .getSystemService(ActivityManager::class.java)
            .runningAppProcesses
            .any {
                it.processName == packageName && it.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
            }
    }