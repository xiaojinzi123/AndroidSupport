package com.xiaojinzi.support.demo

import android.app.Application
import androidx.multidex.MultiDex
import com.xiaojinzi.component.Component
import com.xiaojinzi.component.Config
import com.xiaojinzi.support.ktx.newUUid
import com.xiaojinzi.support.util.LogSupport

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        MultiDex.install(this);

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

    }

}