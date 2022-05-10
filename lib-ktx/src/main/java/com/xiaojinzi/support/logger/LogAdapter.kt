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
 * Provides a common interface to emits logs through. This is a required contract for Logger.
 *
 * @see AndroidLogAdapter
 *
 * @see DiskLogAdapter
 */
interface LogAdapter {
    /**
     * Used to determine whether log should be printed out or not.
     *
     * @param priority is the log level e.g. DEBUG, WARNING
     * @param tag is the given tag for the log message
     *
     * @return is used to determine if log should printed.
     * If it is true, it will be printed, otherwise it'll be ignored.
     */
    fun isLoggable(priority: Int, tag: String?): Boolean

    /**
     * Each log will use this pipeline
     *
     * @param priority is the log level e.g. DEBUG, WARNING
     * @param tag is the given tag for the log message.
     * @param message is the given message for the log message.
     */
    fun log(priority: Int, tag: String?, message: String)
}