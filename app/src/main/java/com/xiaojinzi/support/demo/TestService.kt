package com.xiaojinzi.support.demo

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.xiaojinzi.support.init.AppInstance

class TestService: Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        println("TestService 初始化了")
        AppInstance.app
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

}