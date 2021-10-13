package com.xiaojinzi.support.util

import android.util.Log

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
    fun v(content: String, tag: String = defaultTag, vararg keywords: String) {
        val keywordStr = if (keywords.isNullOrEmpty()) {
            ""
        } else {
            "[${keywords.joinToString()}]"
        }
        Log.v(tag, "$keywordStr $content")
    }

    @JvmOverloads
    fun d(content: String, tag: String = defaultTag, vararg keywords: String) {
        val keywordStr = if (keywords.isNullOrEmpty()) {
            ""
        } else {
            "[${keywords.joinToString()}]"
        }
        Log.d(tag, "$keywordStr $content")
    }

    @JvmOverloads
    fun i(content: String, tag: String = defaultTag, vararg keywords: String) {
        val keywordStr = if (keywords.isNullOrEmpty()) {
            ""
        } else {
            "[${keywords.joinToString()}]"
        }
        Log.i(tag, "$keywordStr $content")
    }

    @JvmOverloads
    fun e(content: String, tag: String = defaultTag, vararg keywords: String) {
        val keywordStr = if (keywords.isNullOrEmpty()) {
            ""
        } else {
            "[${keywords.joinToString()}]"
        }
        Log.e(tag, "$keywordStr $content")
    }

    @JvmOverloads
    fun w(content: String, tag: String = defaultTag, vararg keywords: String) {
        val keywordStr = if (keywords.isNullOrEmpty()) {
            ""
        } else {
            "[${keywords.joinToString()}]"
        }
        Log.w(tag, "$keywordStr $content")
    }

}