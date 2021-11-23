package com.xiaojinzi.support.init

import android.app.Application

object AppInstance {

    lateinit var app: Application

    var isDebug: Boolean = false

}