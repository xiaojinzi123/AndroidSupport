package com.xiaojinzi.support.demo.module.download.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.xiaojinzi.component.anno.RouterAnno
import com.xiaojinzi.support.demo.DOWNLOAD_TEST
import com.xiaojinzi.support.demo.R
import com.xiaojinzi.support.ktx.app
import com.xiaojinzi.support.ktx.newUUid
import com.xiaojinzi.support.ktx.observeOnMainThread
import com.xiaojinzi.support.ktx.subscribeOnIOThread
import com.xiaojinzi.support.download.Downloader
import com.xiaojinzi.support.download.bean.DownloadTask
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.await
import java.io.File
import java.lang.Exception

@RouterAnno(hostAndPath = DOWNLOAD_TEST)
class DownloadTestAct : AppCompatActivity() {

    private val text1 = "下载一个文件并监听进度"
    private val text2 = "下载 N 个文件并监听进度"

    private val downloadViewModel: DownloadViewModel by viewModels()

    private val tvInfo: TextView by lazy { findViewById(R.id.tvInfo) }

    private val btTest1: Button by lazy { findViewById(R.id.btTest1) }
    private val btTest2: Button by lazy { findViewById(R.id.btTest2) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_download_test_act)
    }

    fun test1(view: View) {
        lifecycleScope
            .launch {
                try {
                    val task = DownloadTask(
                        url = "http://pic.netbian.com/uploads/allimg/170725/103840-150095032034c0.jpg",
                        downloadTo = File(
                            app.cacheDir, newUUid() + ".jpg"
                        )
                    )
                    /*val progressDisposable = Downloader.subscribePublishProgress(task.tag)
                        .observeOnMainThread()
                        .subscribeBy { progressTask ->
                            btTest1.text = text1 + " " + progressTask.progress.toInt().toString()
                        }*/
                    // 下载完毕
                    Downloader
                        .downloadProgress(task)
                        .observeOnMainThread()
                        .doOnNext { progress ->
                            btTest1.text = text1 + " " + progress.toInt().toString()
                        }
                        .ignoreElements()
                        .subscribeOnIOThread()
                        .await()
                    btTest1.text = "$text1 下载完成"
                    tvInfo.text = "下载完成的地址：" + task.downloadTo.path
                    // progressDisposable.dispose()
                } catch (e: Exception) {
                    btTest1.text = "$text1 下载失败"
                }
            }
            .invokeOnCompletion { }
    }

    fun test2(view: View) {
        lifecycleScope.launch {
            try {
                // 首先准备多个下载任务
                val downloadUrl =
                    "http://pic.netbian.com/uploads/allimg/170725/103840-150095032034c0.jpg"
                val tasks = listOf(
                    downloadUrl,
                    downloadUrl,
                    downloadUrl,
                    downloadUrl,
                    downloadUrl,
                    downloadUrl,
                    downloadUrl,
                    downloadUrl,
                    downloadUrl,
                    downloadUrl
                )
                    .map { url ->
                        DownloadTask(
                            url = url,
                            downloadTo = File(
                                app.cacheDir, newUUid() + ".jpg"
                            )
                        )
                    }

                val progressDisposable =
                    Downloader.subscribePublishCombineProgress(tasks.map { it.tag })
                        .observeOnMainThread()
                        .subscribeBy { progress ->
                            btTest2.text = text2 + " " + progress.toInt().toString()
                        }

                val resultList = Downloader.downloadList(tasks).await()

                tvInfo.text = "下载完成的地址：" + resultList
                    .map { it.task.downloadTo.path }
                    .reduceOrNull { acc, s -> acc + "\n" + s }
                btTest2.text = "$text2 下载完成"
                progressDisposable.dispose()

            } catch (e: Exception) {
                btTest2.text = "$text2 下载失败(有一个失败就表示全部失败)"
            }
        }
    }

}