package com.xiaojinzi.support.demo

import com.xiaojinzi.support.architecture.mvvm1.BaseUseCase
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCaseImpl
import com.xiaojinzi.support.architecture.mvvm1.CommonUseCase
import com.xiaojinzi.support.architecture.mvvm1.CommonUseCaseImpl

interface MainUseCase: BaseUseCase, CommonUseCase {
}

class MainUseCaseImpl(
    private val commonUseCase: CommonUseCase = CommonUseCaseImpl(),
): BaseUseCaseImpl(),
    CommonUseCase by commonUseCase,
    MainUseCase {

    override fun destroy() {
        super.destroy()
        commonUseCase.destroy()
    }

}