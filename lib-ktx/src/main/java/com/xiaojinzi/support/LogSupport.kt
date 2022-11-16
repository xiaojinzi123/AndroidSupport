package com.xiaojinzi.support

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
        val thread = Thread.currentThread()
        val threadInfo = "[threadId=${thread.id}, threadName=${thread.name}]"
        Log.v(tag, "$threadInfo $keywordStr $content")
    }

    @JvmOverloads
    fun d(content: String, tag: String = defaultTag, vararg keywords: String) {
        val keywordStr = if (keywords.isNullOrEmpty()) {
            ""
        } else {
            "[${keywords.joinToString()}]"
        }
        val thread = Thread.currentThread()
        val threadInfo = "[threadId=${thread.id}, threadName=${thread.name}]"
        Log.d(tag, "$threadInfo $keywordStr $content")
    }

    @JvmOverloads
    fun i(content: String, tag: String = defaultTag, vararg keywords: String) {
        val keywordStr = if (keywords.isNullOrEmpty()) {
            ""
        } else {
            "[${keywords.joinToString()}]"
        }
        val thread = Thread.currentThread()
        val threadInfo = "[threadId=${thread.id}, threadName=${thread.name}]"
        Log.i(tag, "$threadInfo $keywordStr $content")
    }

    @JvmOverloads
    fun e(content: String, tag: String = defaultTag, vararg keywords: String) {
        val keywordStr = if (keywords.isNullOrEmpty()) {
            ""
        } else {
            "[${keywords.joinToString()}]"
        }
        val thread = Thread.currentThread()
        val threadInfo = "[threadId=${thread.id}, threadName=${thread.name}]"
        Log.e(tag, "$threadInfo $keywordStr $content")
    }

    @JvmOverloads
    fun w(content: String, tag: String = defaultTag, vararg keywords: String) {
        val keywordStr = if (keywords.isNullOrEmpty()) {
            ""
        } else {
            "[${keywords.joinToString()}]"
        }
        val thread = Thread.currentThread()
        val threadInfo = "[threadId=${thread.id}, threadName=${thread.name}]"
        Log.w(tag, "$threadInfo $keywordStr $content")
    }

}