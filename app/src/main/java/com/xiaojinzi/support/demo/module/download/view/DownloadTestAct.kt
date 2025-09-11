package com.xiaojinzi.support.demo.module.download.view

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.xiaojinzi.component.anno.RouterAnno
import com.xiaojinzi.support.demo.DOWNLOAD_TEST
import com.xiaojinzi.support.demo.R
import com.xiaojinzi.support.download.DownloadState
import com.xiaojinzi.support.download.DownloadTask
import com.xiaojinzi.support.download.OkHttpDownloadProvider
import com.xiaojinzi.support.download.onError
import com.xiaojinzi.support.download.onSuccess
import com.xiaojinzi.support.ktx.LogSupport
import com.xiaojinzi.support.ktx.app
import com.xiaojinzi.support.ktx.newUUid
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.File

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
                val task = DownloadTask.UrlDownloadTask(
                    url = "https://xiaojinzi.oss-cn-shanghai.aliyuncs.com/temp/1_1.mp4",
                    downloadTo = File(
                        app.cacheDir, newUUid() + ".mp4"
                    ),
                )
                /*val progressDisposable = Downloader.subscribePublishProgress(task.tag)
                    .observeOnMainThread()
                    .subscribeBy { progressTask ->
                        btTest1.text = text1 + " " + progressTask.progress.toInt().toString()
                    }*/
                // 下载完毕
                OkHttpDownloadProvider
                    .download(
                        task = task,
                    )
                    .onSuccess {
                        btTest1.text = "$text1 下载完成"
                        tvInfo.text = "下载完成的地址：" + task.downloadTo.path
                    }.onError {
                        btTest1.text = "$text1 下载失败"
                    }
            }
            .invokeOnCompletion { }
    }

    fun test2(view: View) {
        lifecycleScope.launch {
            try {
                // 首先准备多个下载任务
                val downloadUrl = "https://xiaojinzi.oss-cn-shanghai.aliyuncs.com/temp/1_1.mp4"
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
                ).map { url ->
                    DownloadTask.UrlDownloadTask(
                        url = url,
                        downloadTo = File(
                            app.cacheDir, newUUid() + ".mp4"
                        )
                    )
                }

                val progressDisposable =
                    OkHttpDownloadProvider
                        .subscribeCombinedProgressEvent(*tasks.map { it.tag }.toTypedArray())
                        .onEach { progress ->
                            btTest2.text = text2 + " " + progress.progress.toInt().toString()
                        }
                        .launchIn(scope = this)

                val resultList = OkHttpDownloadProvider.downloadList(
                    tasks = tasks,
                ).map { it as DownloadState.EndState.DownloadSuccess }

                tvInfo.text = "下载完成的地址：" + resultList
                    .map { (it.task.basicTask as? DownloadTask.UrlDownloadTask)?.downloadTo?.path }
                    .reduceOrNull { acc, s -> acc + "\n" + s }
                btTest2.text = "$text2 下载完成"
                progressDisposable.cancel()

            } catch (e: Exception) {
                e.printStackTrace()
                btTest2.text = "$text2 下载失败(有一个失败就表示全部失败)"
            }
        }
    }

    init {

        LogSupport.d(
            tag = "123123",
            content = "DownloadTestAct 初始化了"
        )

    }

}