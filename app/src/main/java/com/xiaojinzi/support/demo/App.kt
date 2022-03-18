package com.xiaojinzi.support.demo

import android.app.Application
import android.content.Context
import com.xiaojinzi.component.Component
import com.xiaojinzi.component.Config
import com.xiaojinzi.support.init.AppInstance
import com.xiaojinzi.support.ktx.AppInitSupport
import com.xiaojinzi.support.ktx.AppInitTask
import com.xiaojinzi.support.ktx.AppScope
import com.xiaojinzi.support.util.LogSupport
import kotlinx.coroutines.launch

class App : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        AppInstance.app = this
    }

    override fun onCreate() {
        super.onCreate()

        Component.init(
            BuildConfig.DEBUG,
            Config.with(this)
                // 表示是否采用 Gradle 插件配置的方式加载模块
                .optimizeInit(true)
                // 自动加载所有模块, 依赖上面的 optimizeInit(true)
                .autoRegisterModule(true)
                // 执行构建
                .build()
        )

        LogSupport.e(content = "天呐", keywords = arrayOf("我的", "你的"))

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

        }

    }

}