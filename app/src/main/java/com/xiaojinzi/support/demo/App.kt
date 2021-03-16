package com.xiaojinzi.support.demo

import android.app.Application
import androidx.multidex.MultiDex
import com.quvideo.mobile.platform.httpcore.QuVideoDomain
import com.quvideo.mobile.platform.httpcore.QuVideoHttpCore
import com.quvideo.mobile.platform.httpcore.provider.HttpConfig
import com.quvideo.mobile.platform.httpcore.provider.HttpParams
import com.xiaojinzi.component.Component
import com.xiaojinzi.component.Config
import com.xiaojinzi.support.ktx.app

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        MultiDex.install(this);

        getExternalFilesDir(null)

        val httpConfig = HttpConfig()
        httpConfig.productId = 2
        httpConfig.appKey = "100222TS"
        httpConfig.isDebug = true
        QuVideoHttpCore.init(app, httpConfig)
        QuVideoHttpCore.setHttpClientProvider {
            val httpParams = HttpParams()
            //QuVideoDomain quVideoDomain = new QuVideoDomain(QuVideoDomain.DomainType.TYPE_PLATFORM_US);
            val quVideoDomain = QuVideoDomain("http://medi-qa-xjp.rthdo.com")
            httpParams.domain = quVideoDomain
            httpParams.deviceId = "test"
            httpParams.productId = 2;
            httpParams
        }

        Component.init(
            BuildConfig.DEBUG,
            Config.with(this)
                // 表示是否采用 Gradle 插件配置的方式加载模块
                .optimizeInit(true)
                // 自动加载所有模块, 依赖上面的 optimizeInit(true)
                .autoRegisterModule(true)
                // 执行构建
                .build()
        );

    }

}