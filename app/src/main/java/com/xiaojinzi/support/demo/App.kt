package com.xiaojinzi.support.demo

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Process
import androidx.appcompat.app.AppCompatActivity
import com.xiaojinzi.component.Component
import com.xiaojinzi.component.Config
import com.xiaojinzi.component.support.ASMUtil
import com.xiaojinzi.support.activity_stack.ActivityStack
import com.xiaojinzi.support.architecture.mvvm1.UseCaseCheck
import com.xiaojinzi.support.init.AppInstance
import com.xiaojinzi.support.init.CheckInit
import com.xiaojinzi.support.ktx.AppInitSupport
import com.xiaojinzi.support.ktx.AppInitTask
import com.xiaojinzi.support.ktx.AppScope
import com.xiaojinzi.support.ktx.ComponentLifecycleCallback
import com.xiaojinzi.support.ktx.HotEventFlow
import com.xiaojinzi.support.ktx.LogSupport
import com.xiaojinzi.support.ktx.MemoryCache
import com.xiaojinzi.support.ktx.MemoryCacheConfig
import com.xiaojinzi.support.ktx.app
import com.xiaojinzi.support.logger.AndroidLogAdapter
import com.xiaojinzi.support.logger.Logger
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

/**
 * 获取进程的名字
 */
private fun Context.getProcessName(): String? {
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Application.getProcessName()
        } else {
            val pid = Process.myPid()
            val am = this.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
            am?.runningAppProcesses
                ?.find { it.pid == pid }
                ?.processName
        }
    } catch (_: Exception) {
        null
    }
}

class App : Application() {

    private val testFlow: HotEventFlow<Int> = flowOf(1)

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        AppInstance.app = this
        LogSupport.logAble = BuildConfig.DEBUG
    }

    override fun onCreate() {
        super.onCreate()

        val processName = this.getProcessName()
        if (processName != packageName) {
            return
        }

        (app.getSystemService(AppCompatActivity.ACTIVITY_SERVICE) as ActivityManager).apply {
            val appTask = appTasks.firstOrNull()
            LogSupport.d(
                tag = "123123",
                content = "${appTask?.taskInfo}"
            )
            if (appTasks.size > 1 || appTasks.any { it.taskInfo.numActivities > 1 }) {
                appTasks.forEach { it.finishAndRemoveTask() }
                val bootIntent = Intent("xxxxxxx_app_reboot")
                bootIntent.addCategory(Intent.CATEGORY_DEFAULT)
                bootIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                bootIntent.putExtra("action", "xxxxxxxx_app_main")
                bootIntent.putExtra("category", Intent.CATEGORY_DEFAULT)
                app.startActivity(bootIntent)
                Process.killProcess(Process.myPid())
            } else {
                LogSupport.d(
                    tag = "123123",
                    content = "正常启动"
                )
            }
        }

        /*CheckInit.init(
            app = this,
            bootActAction = "xxxxxxxx_app_main",
            bootActCategory = Intent.CATEGORY_DEFAULT,
            rebootActAction = "xxxxxxx_app_reboot",
            rebootActCategory = Intent.CATEGORY_DEFAULT,
        )*/

        MemoryCache.setCacheConfig(
            keyClass = CacheUserKey::class,
            config = MemoryCacheConfig(
                cacheTime = 10 * 1000,
                updateCallable = {
                    delay(1000)
                    "${it.url} 数据: ${System.currentTimeMillis()}"
                },
            )
        )

        registerActivityLifecycleCallbacks(ComponentLifecycleCallback())
        ActivityStack.init(
            app = this,
        )

        LogSupport.d(
            tag = "app",
            content = "moduleNames = ${ASMUtil.getModuleNames()}",
        )

        Component.init(
            application = this,
            isDebug = true,
            config = Config.Builder()
                // 表示是否采用 Gradle 插件配置的方式加载模块
                .optimizeInit(true)
                // 自动加载所有模块, 依赖上面的 optimizeInit(true)
                .autoRegisterModule(true)
                // 执行构建
                .build()
        )

        LogSupport.e(content = "天呐", keywords = arrayOf("我的", "你的"))

        Logger.addLogAdapter(AndroidLogAdapter())
        Logger.json(json = "{\"name\":\"xiaojinzi\",\"age\":18}")

        LogSupport.d(
            tag = "123123",
            content = "正常初始化了 Application"
        )

        AppScope.launch {

            AppInitSupport
                .addTask(
                    AppInitTask {
                        println("任务1")
                    },
                    AppInitTask {
                        println("任务2")
                    },
                    AppInitTask(
                        priority = 1,
                    ) {
                        println("任务3")
                    },
                )
                .execute()

            while (true) {
                delay(timeMillis = 2000)
                UseCaseCheck.printUseCase()
            }

        }

    }

}