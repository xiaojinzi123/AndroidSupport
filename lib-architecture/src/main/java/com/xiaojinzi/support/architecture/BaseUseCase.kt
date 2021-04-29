package com.xiaojinzi.support.architecture

import androidx.annotation.CallSuper
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

interface BaseUseCase {

  /**
   * 销毁
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
    scope.cancel()
    disposables.dispose()
  }

}