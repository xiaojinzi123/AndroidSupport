package com.xiaojinzi.support.demo

import android.app.Application
import com.xiaojinzi.component.anno.ModuleAppAnno
import com.xiaojinzi.component.application.IApplicationLifecycle
import com.xiaojinzi.component.application.IModuleNotifyChanged

@ModuleAppAnno
class AppModuleApplication : IApplicationLifecycle, IModuleNotifyChanged {

    override fun onCreate(app: Application) {
    }

    override fun onDestroy() {
    }

    override fun onModuleChanged(app: Application) {
    }

}