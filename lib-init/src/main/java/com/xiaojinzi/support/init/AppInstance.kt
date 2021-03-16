package com.xiaojinzi.support.init

import android.app.Application

object AppInstance {

    internal var _app: Application? = null

    var isDebug: Boolean = false

    fun getApp(): Application{
        return _app!!
    }

}