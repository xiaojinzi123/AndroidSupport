package com.xiaojinzi.support.ktx

import android.media.MediaMetadataRetriever
import com.xiaojinzi.support.annotation.TimeValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 获取媒体的时长
 */
@TimeValue(value = TimeValue.Type.MILLISECOND)
suspend fun getMediaDuration(path: String): Long? {
     return withContext(context = Dispatchers.IO) {
        // MediaMetadataRetriever 在 API 29 之前没有实现 AutoCloseable，不能用 use {}
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(path)
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull()
        } catch (e: Exception) {
            null
        } finally {
            try {
                retriever.release()
            } catch (ignore: Exception) {
                // ignore
            }
        }
    }
}