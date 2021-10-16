package com.xiaojinzi.support.architecture.mvvm1

import android.R.id
import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.CallSuper
import androidx.annotation.UiThread
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.xiaojinzi.support.annotation.ViewLayer
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * Activity 的基类
 *
 * @param <VM> 使用的 [BaseViewModel]
 */
@ViewLayer
abstract class BaseAct<VM : BaseViewModel> : AppCompatActivity(), BaseView {

    /**
     * TAG, 打印用
     */
    protected var mActivityTag: String = this.javaClass.simpleName

    /*上下文*/
    protected lateinit var mContext: FragmentActivity

    /**
     * 一个 [CompositeDisposable] 的容器, 会在 [.onDestroy] 方法中 dispose 掉.
     * 并且后续添加的都会被 dispose
     */
    protected val mDisposables = CompositeDisposable()

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
            mViewModel = ViewModelProvider(this).get(viewModelClass)
        }
        // CommonUseCase 中的订阅一下
        (mViewModel as? CommonUseCase)?.let { commonUseCase ->
            if (isSubscribeViewModelLoading()) {
                commonUseCase.isLoadingObservable
                    .onEach {
                        showLoading(isShow = it)
                    }
                    .launchIn(lifecycleScope)
            }
            if (isSubscribeViewModelTip()) {
                commonUseCase.tipObservable
                    .onEach {
                        onTip(it)
                    }
                    .launchIn(lifecycleScope)
            }
            commonUseCase.activityFinishEventObservable
                .onEach {
                    onActivityFinishEvent()
                }
                .launchIn(lifecycleScope)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == id.home) {
            finish()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }


    @UiThread
    protected open fun onActivityFinishEvent() {
        finish()
    }

    @CallSuper
    override fun onDestroy() {
        super.onDestroy()
        mDisposables.dispose()
    }

}