package com.xiaojinzi.support.architecture.mvvm1

import androidx.annotation.CallSuper
import com.xiaojinzi.support.util.ComponentActivityStack
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive

/**
 * 这个接口中的方法不可以新增了哦!!!
 * 这个 BaseUseCase 由于每一个 UseCase 都要继承
 * 但是 UseCase 之间又可能随意组合使用. 比如下面的例子：
 * class UseCase1: BaseUseCase
 * class UseCase2: BaseUseCase
 * // 逻辑类 UseCase3 组合 UseCase1 和 UseCase2 的功能
 * class UseCase3: BaseUseCase, UseCase1, UseCase2
 *
 * class UseCase3Impl: BaseUseCaseImpl(), UseCase3,
 * UseCase1 by UseCase1Impl,
 * UseCase2 by UseCase2Impl {
 *      // some code
 * }
 *
 * 此时 UseCase3Impl 就会报一个错, 因为 destroy 方法在多个委托中都有
 * UseCase3Impl 不清楚要转发给谁,
 * 所以 BaseUseCase 中所有定义的方法都会有这个问题. 所以除了必要的 destroy 方法
 * 抽离了一个 CommonUseCase 来定义其他方法. 通过构造方法传递来使用.
 */
interface BaseUseCase {

    /**
     * 销毁, 此方法可能会多次执行
     */
    fun destroy()

}

/**
 * 基础的业务接口实现
 */
open class BaseUseCaseImpl : BaseUseCase {

    val scope: CoroutineScope = MainScope()
    val disposables: CompositeDisposable = CompositeDisposable()

    @CallSuper
    override fun destroy() {
        UseCaseCheck.removeUseCase(useCaseName = this.toString())
        if (scope.isActive) {
            scope.cancel()
        }
        if (!disposables.isDisposed) {
            disposables.dispose()
        }
    }

    init {
        UseCaseCheck.addUseCase(useCaseName = this.toString())
    }

}