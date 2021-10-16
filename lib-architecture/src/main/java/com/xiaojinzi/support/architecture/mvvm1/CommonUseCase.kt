package com.xiaojinzi.support.architecture.mvvm1

import androidx.annotation.Keep
import androidx.annotation.StringRes
import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.support.annotation.ViewModelLayer
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

enum class TipType {
    Toast
}

@Keep
data class TipBean(
    val type: TipType = TipType.Toast,
    val contentResId: Int? = null,
    val content: String? = null
)

/**
 * 定义了一些公共的逻辑,
 */
@ViewModelLayer
interface CommonUseCase : BaseUseCase {

    fun showLoading(isShow: Boolean)

    fun tip(tipBean: TipBean)

    fun toast(@StringRes contentResId: Int) {
        tip(TipBean(type = TipType.Toast, contentResId = contentResId))
    }

    fun toast(content: String) {
        tip(TipBean(type = TipType.Toast, content = content))
    }

    fun postActivityFinishEvent()

    /**
     * 这是 UI 的表现, 所以这个模式是 Behavior 的
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR)
    val isLoadingObservable: Flow<Boolean>

    @HotObservable(HotObservable.Pattern.PUBLISH)
    val tipObservable: Flow<TipBean>

    @HotObservable(HotObservable.Pattern.PUBLISH)
    val activityFinishEventObservable: Flow<Unit>

}

/**
 * 不可以被继承, 只能被创建的方式使用
 */
class CommonUseCaseImpl : BaseUseCaseImpl(), CommonUseCase {

    /**
     * 是否是加载状态, 用于控制 View 层的 loading 动画
     */
    override val isLoadingObservable = MutableStateFlow(false)

    /**
     * 提示
     */
    override val tipObservable = MutableSharedFlow<TipBean>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    override val activityFinishEventObservable = MutableSharedFlow<Unit>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    override fun showLoading(isShow: Boolean) {
        isLoadingObservable.tryEmit(isShow)
    }

    override fun tip(tipBean: TipBean) {
        tipObservable.tryEmit(tipBean)
    }

    override fun postActivityFinishEvent() {
        activityFinishEventObservable.tryEmit(Unit)
    }

}
