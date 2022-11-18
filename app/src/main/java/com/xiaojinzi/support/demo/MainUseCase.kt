package com.xiaojinzi.support.demo

import com.xiaojinzi.support.ktx.InitOnceData
import com.xiaojinzi.support.ktx.MutableInitOnceData
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCase
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCaseImpl
import com.xiaojinzi.support.architecture.mvvm1.CommonUseCase
import com.xiaojinzi.support.architecture.mvvm1.CommonUseCaseImpl
import com.xiaojinzi.support.ktx.initOnceData
import com.xiaojinzi.support.ktx.ErrorIgnoreContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

interface MainUseCase: BaseUseCase, CommonUseCase {

    val testInitData1: InitOnceData<String?>

    val testInitData2: InitOnceData<Int?>

}

class MainUseCaseImpl(
    private val commonUseCase: CommonUseCase = CommonUseCaseImpl(),
): BaseUseCaseImpl(),
    CommonUseCase by commonUseCase,
    MainUseCase {

    override val testInitData1 = MutableInitOnceData<String?>(
        initValue = "我是一个猪"
    )

    override val testInitData2 = testInitData1.initOnceData(
        scope = scope
    ) {
        it?.length
    }

    override fun destroy() {
        super.destroy()
        commonUseCase.destroy()
    }

    init {

        scope.launch(context = ErrorIgnoreContext) {

            println("testInitData1.value = ${testInitData1.valueStateFlow.first()}")
            println("testInitData2.value = ${testInitData2.valueStateFlow.first()}")

        }

    }

}