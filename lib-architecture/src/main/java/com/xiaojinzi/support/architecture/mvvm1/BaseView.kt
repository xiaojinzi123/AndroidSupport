package com.xiaojinzi.support.architecture.mvvm1

import androidx.annotation.UiThread
import com.xiaojinzi.support.annotation.ViewLayer

@ViewLayer
interface BaseView {

    /**
     * 是否订阅 ViewModel 中的 loading 状态
     */
    fun isSubscribeViewModelLoading(): Boolean {
        return false
    }

    fun isSubscribeViewModelTip(): Boolean {
        return false
    }

    @UiThread
    fun onTip(content: TipBean) {
        // empty
    }

    @UiThread
    fun showLoading(isShow: Boolean) {
        // empty
    }

}