package com.xiaojinzi.support.architecture.mvvm1

import androidx.annotation.CallSuper
import androidx.lifecycle.ViewModel
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCase
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject

/**
 * 每一个 [ViewModel] 的基类. 定义了一些公用的方法
 * 如何创建：new ViewModelProvider(FragmentActivity).get(XxxViewModel.class);
 * 所有的 ViewModel 都应该为 [Activity] 减轻负担, 承载 UI 相关的业务逻辑和 UI 相关的数据.
 * 而数据我们可以使用"Hot Observable" 模式将数据变为具备通知能力的对象
 * 可以利用 [LiveData] 或者 [Subject] 实现.
 * [Subject] 中和 [LiveData] 对应的是 [BehaviorSubject]
 * 都是表示订阅最近发射出来的一个信号. 如果只想收到订阅之后的,
 * 请使用 [PublishSubject]. 整体其实 Google 官方的是不够的, 只有一个
 * [LiveData] 模式. 总体上 RxJava 的模式比较全,
 * 具体可以去看 [Subject] 这个抽象类的实现
 *
 * @Note: ViewModel 不是用来承载业务逻辑的, 只是适合处理 UI 相关的数据和保存 UI 相关的数据及其需要保存的引用
 * @see BaseAct
 * @see BaseFrag
 */
abstract class BaseViewModel
@JvmOverloads constructor(
) : ViewModel(){

    val disposables: CompositeDisposable = CompositeDisposable()

    @CallSuper
    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
        if (this is BaseUseCase) {
            destroy()
        }
    }

}