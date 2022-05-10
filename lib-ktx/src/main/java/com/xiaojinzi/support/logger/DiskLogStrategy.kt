package com.xiaojinzi.support.logger

import android.os.*
import com.xiaojinzi.support.logger.LogAdapter
import com.xiaojinzi.support.logger.FormatStrategy
import com.xiaojinzi.support.logger.PrettyFormatStrategy
import com.xiaojinzi.support.logger.LogStrategy
import com.xiaojinzi.support.logger.CsvFormatStrategy
import com.xiaojinzi.support.logger.DiskLogStrategy.WriteHandler
import com.xiaojinzi.support.logger.DiskLogStrategy
import com.xiaojinzi.support.logger.LogcatLogStrategy
import com.xiaojinzi.support.logger.LoggerPrinter
import org.json.JSONObject
import org.json.JSONArray
import org.json.JSONException
import java.io.File
import java.io.FileWriter
import java.io.IOException

/**
 * Abstract class that takes care of background threading the file log operation on Android.
 * implementing classes are free to directly perform I/O operations there.
 *
 * Writes all logs to the disk with CSV format.
 */
class DiskLogStrategy(handler: Handler) : LogStrategy {
    private val handler: Handler
    override fun log(level: Int, tag: String?, message: String) {
        Utils.checkNotNull(message)

        // do nothing on the calling thread, simply pass the tag/msg to the background thread
        handler.sendMessage(handler.obtainMessage(level, message))
    }

    internal class WriteHandler(looper: Looper, folder: String, maxFileSize: Int) :
        Handler(Utils.checkNotNull(looper)) {
        private val folder: String
        private val maxFileSize: Int
        override fun handleMessage(msg: Message) {
            val content = msg.obj as String
            var fileWriter: FileWriter? = null
            val logFile = getLogFile(folder, "logs")
            try {
                fileWriter = FileWriter(logFile, true)
                writeLog(fileWriter, content)
                fileWriter.flush()
                fileWriter.close()
            } catch (e: IOException) {
                if (fileWriter != null) {
                    try {
                        fileWriter.flush()
                        fileWriter.close()
                    } catch (e1: IOException) { /* fail silently */
                    }
                }
            }
        }

        /**
         * This is always called on a single background thread.
         * Implementing classes must ONLY write to the fileWriter and nothing more.
         * The abstract class takes care of everything else including close the stream and catching IOException
         *
         * @param fileWriter an instance of FileWriter already initialised to the correct file
         */
        @Throws(IOException::class)
        private fun writeLog(fileWriter: FileWriter, content: String) {
            Utils.checkNotNull(fileWriter)
            Utils.checkNotNull(content)
            fileWriter.append(content)
        }

        private fun getLogFile(folderName: String, fileName: String): File {
            Utils.checkNotNull(folderName)
            Utils.checkNotNull(fileName)
            val folder = File(folderName)
            if (!folder.exists()) {
                //TODO: What if folder is not created, what happens then?
                folder.mkdirs()
            }
            var newFileCount = 0
            var newFile: File
            var existingFile: File? = null
            newFile = File(folder, String.format("%s_%s.csv", fileName, newFileCount))
            while (newFile.exists()) {
                existingFile = newFile
                newFileCount++
                newFile = File(folder, String.format("%s_%s.csv", fileName, newFileCount))
            }
            return if (existingFile != null) {
                if (existingFile.length() >= maxFileSize) {
                    newFile
                } else existingFile
            } else newFile
        }

        init {
            this.folder = Utils.checkNotNull(folder)
            this.maxFileSize = maxFileSize
        }
    }

    init {
        this.handler = Utils.checkNotNull(handler)
    }
}