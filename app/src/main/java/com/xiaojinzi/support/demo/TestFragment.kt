package com.xiaojinzi.support.demo

import com.xiaojinzi.support.architecture.mvvm1.BaseFrag
import com.xiaojinzi.support.architecture.mvvm1.BaseViewModel
import com.xiaojinzi.support.architecture.mvvm1.TipBean

class TestFragment: BaseFrag<BaseViewModel>() {

    override fun onTip(content: TipBean) {
    }

    override fun showLoading(isShow: Boolean) {
    }

    override fun onInit() {
        super.onInit()
    }

}