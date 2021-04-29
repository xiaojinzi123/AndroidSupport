package com.xiaojinzi.support.architecture

import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.support.architecture.bean.TipBean
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

/**
 * 让某一个 UseCase 去创建一个 CommonUseCase 对象
 * 然后共享给其他的 UseCase
 */
interface CommonUseCase {

  fun showLoading(isShow: Boolean)

  fun tip(tipBean: TipBean)

  @HotObservable(HotObservable.Pattern.BEHAVIOR)
  fun subscribeBehaviorIsLoading(): Observable<Boolean>

  @HotObservable(HotObservable.Pattern.BEHAVIOR)
  fun subscribeBehaviorTip(): Observable<TipBean>

}

class CommonUseCaseImpl: CommonUseCase {

  /**
   * 是否是加载状态, 用于控制 View 层的 loading 动画
   */
  private val isLoadingBehaviorSubject = BehaviorSubject.createDefault(false)

  /**
   * 提示
   */
  private val tipBehaviorSubject = BehaviorSubject.create<TipBean>()

  override fun showLoading(isShow: Boolean) {
    isLoadingBehaviorSubject.onNext(isShow)
  }

  override fun tip(tipBean: TipBean) {
    tipBehaviorSubject.onNext(tipBean)
  }

  @HotObservable(HotObservable.Pattern.BEHAVIOR)
  override fun subscribeBehaviorIsLoading(): Observable<Boolean> {
    return isLoadingBehaviorSubject
  }

  @HotObservable(HotObservable.Pattern.BEHAVIOR)
  override fun subscribeBehaviorTip(): Observable<TipBean> {
    return tipBehaviorSubject
  }

}