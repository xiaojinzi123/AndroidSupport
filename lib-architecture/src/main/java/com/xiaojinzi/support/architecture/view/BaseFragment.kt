package com.xiaojinzi.support.architecture.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.AnyThread
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import java.util.*

abstract class BaseFragment<VM : BaseViewModel?>(contentLayoutId: Int = 0) : Fragment(contentLayoutId) {
    /**
     * 一个 [Disposable] 的容器, 会在 [.onDestroy] 方法中 dispose 掉.
     * 并且后续添加的都会被 dispose
     */
    protected var mDisposables = CompositeDisposable()
    protected var mContext: FragmentActivity? = null
    protected var mContentView: View? = null
    protected var mViewModel: VM? = null

    protected fun onCreateViewModel(): VM? {
        return null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = activity
    }

    /**
     * 此方法在 [.onCreateView] 之后调用的
     */
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = onCreateViewModel()
        mViewModel?.let { vm ->
            if (isSubscribeViewModelLoading()) {
                mDisposables.add(
                    vm.subscribeBehaviorIsLoading()
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
                mDisposables.add(
                    vm.subscribeBehaviorTip()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { (_, content) -> showToast(content) }
                )
            }
        }
        onInit()
    }

    fun requiredViewModel(): VM {
        return mViewModel!!
    }

    @CallSuper
    protected fun onInit() {
        // 暂时是空的
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (mContentView == null) {
            mContentView = super.onCreateView(inflater, container, savedInstanceState)
        }
        return mContentView
    }

    protected fun isSubscribeViewModelLoading(): Boolean {
        return false
    }

    protected fun isSubscribeViewModelTip(): Boolean {
        return false
    }

    @AnyThread
    protected abstract fun showToast(content: String?)
    protected abstract fun showLoading()
    protected abstract fun dismissLoading()

    override fun onDestroy() {
        super.onDestroy()
        mDisposables.dispose()
    }

}