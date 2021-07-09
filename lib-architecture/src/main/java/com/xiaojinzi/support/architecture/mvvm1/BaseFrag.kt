package com.xiaojinzi.support.architecture.mvvm1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.*

abstract class
BaseFrag<VM : BaseViewModel?>(private val contentLayoutId: Int = 0) :
    Fragment(contentLayoutId), BaseView {

    /**
     * 一个 [Disposable] 的容器, 会在 [.onDestroy] 方法中 dispose 掉.
     * 并且后续添加的都会被 dispose
     */
    protected val mDisposables: CompositeDisposable = CompositeDisposable()
    protected var mContentView: View? = null
    protected var mViewModel: VM? = null

    /**
     * 为什么有这个方法, 是因为 Fragment 可以通过挂载的 Activity 或者 自身Fragment 创建不同作用域的 ViewModel.
     * 所以把这个选择交给了子类去实现
     */
    protected fun onCreateViewModel(): VM? {
        return null
    }

    /**
     * 此方法在 [.onCreateView] 之后调用的
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel = onCreateViewModel()
        (mViewModel as? CommonUseCase)?.let { commonUseCase ->
            if (isSubscribeViewModelLoading()) {
                commonUseCase.isLoadingObservable
                    .onEach {
                        showLoading(isShow = it)
                    }
                    .flowOn(Dispatchers.Main)
                    .launchIn(lifecycleScope)
            }
            if (isSubscribeViewModelTip()) {
                commonUseCase.tipObservable
                    .onEach {
                        onTip(it)
                    }
                    .flowOn(Dispatchers.Main)
                    .launchIn(lifecycleScope)
            }
        }
        onInit()
    }

    fun requiredViewModel(): VM {
        return mViewModel!!
    }

    @CallSuper
    protected open fun onInit() {
        // 暂时是空的
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (mContentView == null) {
            mContentView = super.onCreateView(inflater, container, savedInstanceState)
        }
        return mContentView!!
    }

    override fun onDestroy() {
        super.onDestroy()
        mDisposables.dispose()
    }

}