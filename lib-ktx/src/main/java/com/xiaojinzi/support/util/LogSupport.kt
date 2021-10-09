package com.xiaojinzi.support.util

import android.util.Log
import java.lang.Exception

/**
 * 日志的帮助类
 */
object LogSupport {

    @JvmStatic
    val defaultTag: String
        get() = Exception().run {
            stackTrace
                .firstOrNull { it.className != LogSupport::class.qualifiedName }
                ?.run {
                    "${this.className}.${this.methodName}"
                } ?: ""
        }

    @JvmOverloads
    fun v(content: String, tag: String = defaultTag) {
        Log.v(tag, content)
    }

    @JvmOverloads
    fun d(content: String, tag: String = defaultTag) {
        Log.d(tag, content)
    }

    @JvmOverloads
    fun i(content: String, tag: String = defaultTag) {
        Log.i(tag, content)
    }

    @JvmOverloads
    fun e(content: String, tag: String = defaultTag) {
        Log.e(tag, content)
    }

    @JvmOverloads
    fun w(content: String, tag: String = defaultTag) {
        Log.w(tag, content)
    }

}