package com.xiaojinzi.support.demo.module.download.view

import androidx.lifecycle.ViewModel
import com.xiaojinzi.support.demo.module.download.domain.DownloadUseCase
import com.xiaojinzi.support.demo.module.download.domain.DownloadUseCaseImpl

class DownloadViewModel(downloadUseCase: DownloadUseCase = DownloadUseCaseImpl()) : ViewModel(),
    DownloadUseCase by downloadUseCase {
}