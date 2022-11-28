package com.xiaojinzi.support.ktx

import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream

object SystemAlbum {

    const val MIME_TYPE_JPG = "image/jpeg"
    const val MIME_TYPE_JPEG = "image/jpeg"
    const val MIME_TYPE_PNG = "image/png"

    /**
     * 保存图片到相册
     */
    suspend fun saveImageToAlbum(localFile: File, mimeType: String) {

        withContext(Dispatchers.IO) {

            val contentValues = ContentValues()
            val currTime = System.currentTimeMillis()

            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, localFile.name)
            contentValues.put(MediaStore.MediaColumns.DATE_ADDED, currTime)
            contentValues.put(MediaStore.MediaColumns.DATE_MODIFIED, currTime)
            contentValues.put(MediaStore.MediaColumns.SIZE, localFile.length())

            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, mimeType)

            val targetInsertUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

            // 插入相册
            val uri: Uri = app.contentResolver
                .insert(targetInsertUri, contentValues) ?: return@withContext

            val outputStream = app.contentResolver.openOutputStream(uri) ?: return@withContext

            outputStream.use { outputStream1 ->
                FileInputStream(localFile).use { inputStream1 ->
                    inputStream1.copyTo(outputStream1)
                }
            }
            // 通知系统扫描
            try {
                val scanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                scanIntent.data = uri
                app.sendBroadcast(scanIntent)
            } catch (e: Exception) {
                // ignore
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val cur: Cursor =
                    app.contentResolver.query(uri, null, null, null) ?: return@withContext
                cur.moveToFirst()
                val index = cur.getColumnIndex(MediaStore.MediaColumns.DATA)
                val resultPath = cur.getString(index)
                cur.close()

                MediaScannerConnection.scanFile(
                    app,
                    arrayOf(resultPath),
                    arrayOf(mimeType),
                    null,
                )

                val scanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                scanIntent.data = Uri.fromFile(File(resultPath))
                app.sendBroadcast(scanIntent)
            }

        }
    }

    suspend fun saveImageToAlbumIgnoreError(localFile: File, mimeType: String) {
        saveImageToAlbum(
            localFile = localFile,
            mimeType = mimeType,
        )
    }

}