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
        try {
            MediaMetadataRetriever().use {
                it.setDataSource(path)
                it.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull()
            }
        } catch (e: Exception) {
            null
        }
    }
}