package com.xiaojinzi.support.demo.module.download.domain

import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.support.ktx.app
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

interface DownloadUseCase {

    @HotObservable(HotObservable.Pattern.BEHAVIOR)
    fun subscribeDownloadOneLoading(): Observable<Boolean>

    fun downloadOneTest()

    fun destroy()

}

class DownloadUseCaseImpl : DownloadUseCase {

    private val downloadOneLoading = BehaviorSubject.createDefault(false)
    private val downloadToFolder = app.cacheDir

    private val mScope = MainScope()

    override fun subscribeDownloadOneLoading(): Observable<Boolean> {
        return downloadOneLoading
    }

    override fun downloadOneTest() {
    }

    override fun destroy() {
        mScope.cancel()
    }

}