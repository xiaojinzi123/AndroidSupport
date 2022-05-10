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
import android.util.Log
import com.xiaojinzi.support.logger.LogcatLogStrategy
import com.xiaojinzi.support.logger.LoggerPrinter
import org.json.JSONObject
import org.json.JSONArray
import org.json.JSONException

/**
 * LogCat implementation for [LogStrategy]
 *
 * This simply prints out all logs to Logcat by using standard [Log] class.
 */
class LogcatLogStrategy : LogStrategy {
    override fun log(priority: Int, tag: String?, message: String) {
        var tag = tag
        Utils.checkNotNull(message)
        if (tag == null) {
            tag = DEFAULT_TAG
        }
        Log.println(priority, tag, message)
    }

    companion object {
        const val DEFAULT_TAG = "NO_TAG"
    }
}