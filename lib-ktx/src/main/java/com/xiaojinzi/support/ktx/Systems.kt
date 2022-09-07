package com.xiaojinzi.support.ktx

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.Settings

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

fun shake() {
    shake(40L)
}

fun shakeStrong() {
    shake(50L, 255)
}

@SuppressLint("MissingPermission")
fun shake(milliseconds: Long, amplitude: Int = VibrationEffect.DEFAULT_AMPLITUDE) {
    val vib: Vibrator? = app.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vib?.vibrate(
            VibrationEffect.createOneShot(
                milliseconds, amplitude
            ),
            null
        )
    } else {
        vib?.vibrate(milliseconds)
    }
}