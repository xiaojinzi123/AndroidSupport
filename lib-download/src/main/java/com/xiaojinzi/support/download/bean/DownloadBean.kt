package com.xiaojinzi.support.download.bean

import androidx.annotation.Keep
import com.xiaojinzi.support.ktx.newUUid
import java.io.File
import java.io.IOException

/**
 * 下载完成
 */
@Keep
open class DownloadCompletedTask(val task: DownloadTask)

/**
 * 下载失败的异常
 */
@Keep
class DownloadFailException(
    message: String? = null,
    cause: Throwable? = null
) : IOException(message, cause)

/**
 * 下载失败
 */
@Keep
class DownloadFailTask(
    val task: DownloadTask,
    val error: DownloadFailException
)

/**
 * 下载进度
 */
@Keep
class DownloadProgressTask(
    val task: DownloadTask,
    val progress: Float
)

/**
 * 下载的任务
 */
@Keep
open class DownloadTask @JvmOverloads constructor(
    val tag: String = newUUid(),
    val url: String,
    val downloadTo: File,
    // 重试次数, 不能小于 0
    val retryCount: Int = 0
) {
    constructor(target: DownloadTask) : this(
        tag = target.tag,
        url = target.url,
        downloadTo = target.downloadTo,
        retryCount = target.retryCount
    )
}