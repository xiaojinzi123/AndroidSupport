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
 * Determines destination target for the logs such as Disk, Logcat etc.
 *
 * @see LogcatLogStrategy
 *
 * @see DiskLogStrategy
 */
interface LogStrategy {
    /**
     * This is invoked by Logger each time a log message is processed.
     * Interpret this method as last destination of the log in whole pipeline.
     *
     * @param priority is the log level e.g. DEBUG, WARNING
     * @param tag is the given tag for the log message.
     * @param message is the given message for the log message.
     */
    fun log(priority: Int, tag: String?, message: String)
}