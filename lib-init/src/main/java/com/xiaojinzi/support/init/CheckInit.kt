package com.xiaojinzi.support.init

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.os.Process
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

/**
 * 标记一个 Activity 是启动界, 这样会自动标记 [CheckInit.isPassedBootView] 为 true
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class BootView

/**
 * 标记一个 Activity 不检查恢复
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class UnCheckInit

class RebootAct : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        doReboot(bundle = intent.extras!!)
    }

    private fun doReboot(bundle: Bundle) {
        val intent = Intent(bundle.getString("action"))
        intent.addCategory(bundle.getString("category"))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
        // 杀死自己
        Process.killProcess(Process.myPid())
    }

}

private fun rebootApp(app: Application) {
    val bootIntent = Intent("default_app_reboot")
    bootIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    bootIntent.addCategory(Intent.CATEGORY_DEFAULT)
    bootIntent.putExtra("action", "default_app_main")
    bootIntent.putExtra("category", Intent.CATEGORY_DEFAULT)
    app.startActivity(bootIntent)
    Process.killProcess(Process.myPid())
}

class CheckInitLifecycleCallback : Application.ActivityLifecycleCallbacks {
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        // 检测是否是恢复的情况, 如果是, 那么杀死进程重启自己
        val bootView = activity.javaClass.getAnnotation(BootView::class.java)
        if (bootView == null) {
            val unCheckInit = activity.javaClass.getAnnotation(UnCheckInit::class.java)
            if (unCheckInit == null) {
                if (!CheckInit.isPassedBootView) {
                    rebootApp(app = activity.application)
                }
            }
        } else {
            CheckInit.isPassedBootView = true
        }
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }

}

object CheckInit {

    var isPassedBootView: Boolean = false

    fun init(app: Application) {
        app.registerActivityLifecycleCallbacks(CheckInitLifecycleCallback())
    }

}