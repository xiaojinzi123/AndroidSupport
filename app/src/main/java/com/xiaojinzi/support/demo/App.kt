package com.xiaojinzi.support.demo

import android.app.Application
import android.content.Context
import android.content.Intent
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
import com.xiaojinzi.support.ktx.LogSupport
import com.xiaojinzi.support.ktx.MemoryCache
import com.xiaojinzi.support.ktx.MemoryCacheConfig
import com.xiaojinzi.support.logger.AndroidLogAdapter
import com.xiaojinzi.support.logger.Logger
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class App : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        AppInstance.app = this
        LogSupport.logAble = BuildConfig.DEBUG
    }

    override fun onCreate() {
        super.onCreate()

        CheckInit.init(
            app = this,
            bootActAction = "xxxxxxxx_app_main",
            bootActCategory = Intent.CATEGORY_DEFAULT,
            rebootActAction = "xxxxxxx_app_reboot",
            rebootActCategory = Intent.CATEGORY_DEFAULT,
        )

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