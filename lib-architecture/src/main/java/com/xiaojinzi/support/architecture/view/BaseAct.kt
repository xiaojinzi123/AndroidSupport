package com.xiaojinzi.support.architecture.view

import android.os.Bundle
import androidx.annotation.AnyThread
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

/**
 * Activity 的基类
 *
 * @param <VM> 使用的 [BaseViewModel]
</VM> */
open abstract class BaseAct<VM : BaseViewModel> : AppCompatActivity() {

  protected var activityTag = this.javaClass.simpleName

  /*上下文*/
  protected lateinit var mContext: FragmentActivity

  /**
   * 一个 [Disposable] 的容器, 会在 [.onDestroy] 方法中 dispose 掉.
   * 并且后续添加的都会被 dispose
   */
  protected val disposables = CompositeDisposable()

  protected var mViewModel: VM? = null

  protected open fun getViewModelClass(): Class<VM>? {
    return null
  }

  fun requiredViewModel(): VM {
    return mViewModel!!
  }

  @CallSuper
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    mContext = this
    val viewModelClass = getViewModelClass()
    if (viewModelClass != null) {
      mViewModel = ViewModelProvider(this)[viewModelClass]
    }
    mViewModel?.let { targetViewModel ->
      if (isSubscribeViewModelLoading()) {
        disposables.add(
          targetViewModel.subscribeBehaviorIsLoading()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { b: Boolean ->
              if (b) {
                showLoading()
              } else {
                dismissLoading()
              }
            }
        )
      }
      if (isSubscribeViewModelTip()) {
        disposables.add(
          targetViewModel.subscribeBehaviorTip()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { (_, content) -> showToast(content) }
        )
      }
    }
  }

  /**
   * 是否订阅 ViewModel 中的 loading 状态
   */
  open fun isSubscribeViewModelLoading(): Boolean {
    return false
  }

  open fun isSubscribeViewModelTip(): Boolean {
    return false
  }

  @AnyThread
  protected abstract fun showToast(content: String)

  @AnyThread
  protected abstract fun showLoading()

  @AnyThread
  protected abstract fun dismissLoading()

  override fun onDestroy() {
    super.onDestroy()
    disposables.dispose()
  }

}