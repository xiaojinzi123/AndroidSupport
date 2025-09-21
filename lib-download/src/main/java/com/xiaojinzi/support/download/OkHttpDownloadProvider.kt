/*
 * Original Author: fanqie (xiaojinzi@vistring.com)
 * Created:       2024-10-31
 * Last Modified: 2024-11-22
 * Maintainer:    fanqie
 *
 * Copyright (c) 2025 Vistring Inc.
 */
package com.xiaojinzi.support.download

import android.content.Context
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * 基于 [OkHttpClient] 实现的下载器
 */
object OkHttpDownloadProvider : IDownloadProviderBaseImpl() {

    private lateinit var applicationContext: Context

    fun init(context: Context) {
        applicationContext = context.applicationContext
    }

    private val okhttpClient by lazy {
        OkHttpClient()
    }

    private val downloadTaskMap = ConcurrentHashMap<String, Call>()

    override fun doDownload(task: IDownloadTask) {

        task as DownloadTask.UrlDownloadTask

        val request = Request.Builder()
            .url(url = task.url)
            // 禁用 gzip, 这样子 response 中的 content-length 才有
            .addHeader(
                name = "Accept-Encoding",
                value = "identity",
            )
            .apply {
                task.headers.forEach { header ->
                    if (!"Accept-Encoding".equals(other = header.key, ignoreCase = true)) {
                        addHeader(
                            name = header.key,
                            value = header.value,
                        )
                    }
                }
            }
            .build()

        val call = okhttpClient.newCall(request)

        downloadTaskMap[task.tag] = call

        call.enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                downloadTaskMap.remove(task.basicTask.tag)
                postDownloadFail(
                    fail = DownloadState.EndState.DownloadFailed(
                        task = task,
                        error = e,
                    )
                )
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    // 临时文件
                    val temp = File(applicationContext.cacheDir, "${UUID.randomUUID()}.tmp")
                    runCatching {
                        val contentLength = response.body?.contentLength()
                        var downloaded = 0L
                        FileOutputStream(temp).use { outputStream ->
                            response.body?.byteStream()?.use { inputStream ->
                                val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                                var bytesRead: Int
                                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                                    outputStream.write(buffer, 0, bytesRead)
                                    downloaded += bytesRead
                                    postProgress(
                                        progress = DownloadState.ProgressState(
                                            task = task,
                                            totalSize = contentLength,
                                            downloadSize = downloaded,
                                        )
                                    )
                                }
                            }
                        }
                    }.onSuccess {
                        // 重命名
                        temp.renameTo(task.downloadTo)
                        temp.delete()
                        postDownloadSuccess(
                            success = DownloadState.EndState.DownloadSuccess(
                                task = task,
                            )
                        )
                    }.onFailure {
                        temp.delete()
                        postDownloadFail(
                            fail = DownloadState.EndState.DownloadFailed(
                                task = task,
                                error = IOException(
                                    "Download failed, response code: ${response.code}, error: ${it.message}"
                                ),
                            )
                        )
                    }
                } else {
                    postDownloadFail(
                        fail = DownloadState.EndState.DownloadFailed(
                            task = task,
                            error = IOException(
                                "Download failed, response code: ${response.code}"
                            ),
                        )
                    )
                }
                downloadTaskMap.remove(task.basicTask.tag)
            }

        })

    }

    override fun doCancel(task: IDownloadTask) {
        downloadTaskMap.remove(task.basicTask.tag)?.cancel()
    }

}