package com.xiaojinzi.support.logger

import java.io.PrintWriter
import java.io.StringWriter
import java.lang.NullPointerException
import java.net.UnknownHostException
import java.util.*

/**
 * Provides convenient methods to some common operations
 */
internal object Utils {
    /**
     * Returns true if the string is null or 0-length.
     *
     * @param str the string to be examined
     * @return true if str is null or zero length
     */
    fun isEmpty(str: CharSequence?): Boolean {
        return str == null || str.length == 0
    }

    /**
     * Returns true if a and b are equal, including if they are both null.
     *
     * *Note: In platform versions 1.1 and earlier, this method only worked well if
     * both the arguments were instances of String.*
     *
     * @param a first CharSequence to check
     * @param b second CharSequence to check
     * @return true if a and b are equal
     *
     *
     * NOTE: Logic slightly change due to strict policy on CI -
     * "Inner assignments should be avoided"
     */
    fun equals(a: CharSequence?, b: CharSequence?): Boolean {
        if (a === b) return true
        if (a != null && b != null) {
            val length = a.length
            if (length == b.length) {
                return if (a is String && b is String) {
                    a == b
                } else {
                    for (i in 0 until length) {
                        if (a[i] != b[i]) return false
                    }
                    true
                }
            }
        }
        return false
    }

    /**
     * Copied from "android.util.Log.getStackTraceString()" in order to avoid usage of Android stack
     * in unit tests.
     *
     * @return Stack trace in form of String
     */
    fun getStackTraceString(tr: Throwable?): String {
        if (tr == null) {
            return ""
        }

        // This is to reduce the amount of log spew that apps do in the non-error
        // condition of the network being unavailable.
        var t = tr
        while (t != null) {
            if (t is UnknownHostException) {
                return ""
            }
            t = t.cause
        }
        val sw = StringWriter()
        val pw = PrintWriter(sw)
        tr.printStackTrace(pw)
        pw.flush()
        return sw.toString()
    }

    fun logLevel(value: Int): String {
        return when (value) {
            Logger.VERBOSE -> "VERBOSE"
            Logger.DEBUG -> "DEBUG"
            Logger.INFO -> "INFO"
            Logger.WARN -> "WARN"
            Logger.ERROR -> "ERROR"
            Logger.ASSERT -> "ASSERT"
            else -> "UNKNOWN"
        }
    }

    fun toString(obj: Any?): String {
        if (obj == null) {
            return "null"
        }
        if (!obj.javaClass.isArray) {
            return obj.toString()
        }
        if (obj is BooleanArray) {
            return Arrays.toString(obj as BooleanArray?)
        }
        if (obj is ByteArray) {
            return Arrays.toString(obj as ByteArray?)
        }
        if (obj is CharArray) {
            return Arrays.toString(obj as CharArray?)
        }
        if (obj is ShortArray) {
            return Arrays.toString(obj as ShortArray?)
        }
        if (obj is IntArray) {
            return Arrays.toString(obj as IntArray?)
        }
        if (obj is LongArray) {
            return Arrays.toString(obj as LongArray?)
        }
        if (obj is FloatArray) {
            return Arrays.toString(obj as FloatArray?)
        }
        if (obj is DoubleArray) {
            return Arrays.toString(obj as DoubleArray?)
        }
        return if (obj is Array<*>) {
            Arrays.deepToString(obj as Array<Any?>?)
        } else "Couldn't find a correct type for the object"
    }

    fun <T> checkNotNull(obj: T?): T {
        if (obj == null) {
            throw NullPointerException()
        }
        return obj
    }
}