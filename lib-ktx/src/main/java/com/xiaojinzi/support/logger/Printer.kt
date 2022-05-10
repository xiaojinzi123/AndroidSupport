package com.xiaojinzi.support.logger

import com.xiaojinzi.support.logger.LogAdapter
import com.xiaojinzi.support.logger.FormatStrategy
import com.xiaojinzi.support.logger.PrettyFormatStrategy
import com.xiaojinzi.support.logger.LogStrategy
import com.xiaojinzi.support.logger.CsvFormatStrategy
import android.os.Environment
import android.os.HandlerThread
import com.xiaojinzi.support.logger.DiskLogStrategy.WriteHandler
import com.xiaojinzi.support.logger.DiskLogStrategy
import android.os.Looper
import com.xiaojinzi.support.logger.LogcatLogStrategy
import com.xiaojinzi.support.logger.LoggerPrinter
import org.json.JSONObject
import org.json.JSONArray
import org.json.JSONException

/**
 * A proxy interface to enable additional operations.
 * Contains all possible Log message usages.
 */
interface Printer {
    fun addAdapter(adapter: LogAdapter)
    fun t(tag: String?): Printer
    fun d(message: String, vararg args: Any?)
    fun d(`object`: Any?)
    fun e(message: String, vararg args: Any?)
    fun e(throwable: Throwable?, message: String, vararg args: Any?)
    fun w(message: String, vararg args: Any?)
    fun i(message: String, vararg args: Any?)
    fun v(message: String, vararg args: Any?)
    fun wtf(message: String, vararg args: Any?)

    /**
     * Formats the given json content and print it
     */
    fun json(json: String?)

    /**
     * Formats the given xml content and print it
     */
    fun xml(xml: String?)
    fun log(priority: Int, tag: String?, message: String?, throwable: Throwable?)
    fun clearLogAdapters()
}