package com.xiaojinzi.support.demo

import com.xiaojinzi.support.architecture.mvvm1.BaseViewModel

class MainViewModel(
    private val useCase: MainUseCase = MainUseCaseImpl(),
) : BaseViewModel(),
    MainUseCase by useCase {
}