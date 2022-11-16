package com.xiaojinzi.support.architecture.mvvm1

import com.xiaojinzi.support.LogSupport

object UseCaseCheck {

    private val useCaseList = mutableListOf<String>()

    @Synchronized
    fun addUseCase(useCaseName: String) {
        useCaseList.add(element = useCaseName)
    }

    @Synchronized
    fun removeUseCase(useCaseName: String) {
        useCaseList.remove(element = useCaseName)
    }

    @Synchronized
    fun printUseCase() {
        LogSupport.d(
            tag = "UseCaseCheck",
            content = "\n"
        )
        LogSupport.d(
            tag = "UseCaseCheck",
            content = "---------------------- use case start----------------------"
        )
        useCaseList.forEachIndexed { index, s ->
            LogSupport.d(
                tag = "UseCaseCheck",
                content = "use case$index: $s"
            )
        }
        LogSupport.d(
            tag = "UseCaseCheck",
            content = "---------------------- use case end----------------------"
        )
        LogSupport.d(
            tag = "UseCaseCheck",
            content = "\n"
        )
    }

}