package com.xiaojinzi.support.ktx

import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel

class InitOnceViewModel: ViewModel() {
    var isInit: Boolean = false
}

fun FragmentActivity.initOnceUseViewModel(action: () -> Unit) {
    val initViewModel: InitOnceViewModel by this.viewModels()
    if (!initViewModel.isInit) {
        initViewModel.isInit = true
        action.invoke()
    }
}