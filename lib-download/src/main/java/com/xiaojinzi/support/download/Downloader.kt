package com.xiaojinzi.support.download

/**
 * 单例的下载器
 */
object Downloader : DownloadService by DownloadServiceImpl()