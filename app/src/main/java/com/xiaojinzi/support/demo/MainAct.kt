package com.xiaojinzi.support.demo

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.xiaojinzi.component.impl.Router
import com.xiaojinzi.support.ktx.nothing
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainAct : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_main_act)

        lifecycleScope.launch {
            delay(1000)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE), 1)
            }
        }

    }

    fun downloadTest(view: View) {
        startService(
            Intent(
                this@MainAct,
                TestService::class.java
            )
        )
        /*Router.with(this)
            .hostAndPath(DOWNLOAD_TEST)
            .nothing()
            .forward()*/
    }

}