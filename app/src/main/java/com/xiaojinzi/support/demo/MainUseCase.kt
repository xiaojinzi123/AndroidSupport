package com.xiaojinzi.support.demo

import com.xiaojinzi.support.architecture.mvvm1.BaseUseCase
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCaseImpl

interface MainUseCase: BaseUseCase {
}

class MainUseCaseImpl(): BaseUseCaseImpl(), MainUseCase {

}