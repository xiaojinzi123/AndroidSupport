package com.xiaojinzi.support.activity_stack

import android.app.Activity
import android.app.Application
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.Keep
import androidx.lifecycle.Lifecycle
import com.xiaojinzi.support.ktx.AppScope
import com.xiaojinzi.support.ktx.CacheSharedFlow
import com.xiaojinzi.support.ktx.sharedStateIn
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import java.util.Stack

@Retention(
    value = AnnotationRetention.RUNTIME
)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.TYPE,
)
annotation class ActivityFlag(
    vararg val value: String,
)

@Keep
data class ActivityLifecycleEvent(
    val activity: Activity,
    val event: Lifecycle.Event,
)

interface ActivityStackScope {

    /**
     * 还活着的
     */
    fun Activity.isAlive(): Boolean

    /**
     * 有这个 flag 的
     * 这些 flag 必须在这个 Activity 上都有标记
     * 比如：是一班的学生, 并且是短跑冠军的男生
     * Activity1 Flag: [1,2,3,4,5], Activity2 Flag: [3,4,5]
     * Value Flag: 2
     * 满足条件的是：Activity1
     */
    fun Activity.hasFlag(flag: String): Boolean

    /**
     * 有这些 flag 的
     * 这些 flag 必须在这个 Activity 上都有标记
     * 比如：是一班的学生, 并且是短跑冠军的男生
     * Activity1 Flag: [1,2,3,4,5], Activity2 Flag: [3,4,5]
     * Value Flag: [2,3,4]
     * 满足条件的是：Activity1
     */
    fun Activity.hasAllFlag(vararg flags: String): Boolean

    /**
     * 有其中的任意一个 flag
     * 这些 flag 有一个在 Activity 上有标记即可
     * 比如：是一班的学生, 或者是 三好学生
     * Activity1 Flag: [1,2,3,4,5], Activity2 Flag: [2,3,4,5,6]
     * Activity3 Flag: [7,8,9], Activity4 Flag: [1,2,3]
     * Value Flag: [5, 7]
     * 满足条件的是: Activity1, Activity2, Activity3
     */
    fun Activity.hasAnyFlag(vararg flags: String): Boolean

    /**
     * 没有这些 flag 的
     * Activity 没有这些任何一个标记
     * 比如：不是一班的学生, 也不是短跑冠军的男生
     * Activity1 Flag: [1,2,3,4,5], Activity2 Flag: [3,4,5], Activity3 Flag: [5,6]
     * Value Flag: 2
     * 满足条件的是：Activity2, Activity3
     */
    fun Activity.noFlag(flag: String): Boolean

    /**
     * 没有这些 flag 的
     * Activity 没有这些任何一个标记
     * 比如：不是一班的学生, 也不是短跑冠军的男生
     * Activity1 Flag: [1,2,3,4,5], Activity2 Flag: [3,4,5], Activity3 Flag: [5,6]
     * Value Flag: [2,3]
     * 满足条件的是：Activity3
     */
    fun Activity.noAnyFlag(vararg flags: String): Boolean

}

private class ActivityStackScopeImpl : ActivityStackScope {

    override fun Activity.isAlive(): Boolean =
        !if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            this.isFinishing || this.isDestroyed
        } else {
            this.isFinishing
        }

    override fun Activity.hasFlag(flag: String): Boolean {
        return this.hasAnyFlag(flag)
    }

    override fun Activity.hasAllFlag(vararg flags: String): Boolean {
        val activityFlags = this
            .javaClass
            .getAnnotation(ActivityFlag::class.java)
            ?.value ?: emptyArray()
        return flags.all {
            it in activityFlags
        }
    }

    override fun Activity.hasAnyFlag(vararg flags: String) = this
        .javaClass
        .getAnnotation(ActivityFlag::class.java)
        ?.value
        ?.any {
            it in flags
        } ?: false

    override fun Activity.noFlag(flag: String): Boolean {
        return this.noAnyFlag(flag)
    }

    override fun Activity.noAnyFlag(vararg flags: String): Boolean {
        return !hasAnyFlag(flags = flags)
    }

}

private class ActivityLifecycleCallback : Application.ActivityLifecycleCallbacks {

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        ActivityStack.pushActivity(activity)
        ActivityStack.pushLifecycleEvent(
            activity = activity,
            event = Lifecycle.Event.ON_CREATE
        )
    }

    override fun onActivityStarted(activity: Activity) {
        ActivityStack.pushLifecycleEvent(
            activity = activity,
            event = Lifecycle.Event.ON_START,
        )
    }

    override fun onActivityResumed(activity: Activity) {
        ActivityStack.pushLifecycleEvent(
            activity = activity,
            event = Lifecycle.Event.ON_RESUME,
        )
    }

    override fun onActivityPaused(activity: Activity) {
        ActivityStack.pushLifecycleEvent(
            activity = activity,
            event = Lifecycle.Event.ON_PAUSE,
        )
    }

    override fun onActivityStopped(activity: Activity) {
        ActivityStack.pushLifecycleEvent(
            activity = activity,
            event = Lifecycle.Event.ON_STOP,
        )
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        // ignore
    }

    override fun onActivityDestroyed(activity: Activity) {
        ActivityStack.removeActivity(activity)
        ActivityStack.pushLifecycleEvent(
            activity = activity,
            event = Lifecycle.Event.ON_DESTROY,
        )
    }

}

/**
 * Activity 栈
 *
 * @author xiaojinzi
 */
object ActivityStack {

    const val TAG = "ActivityStack"

    private var isDebug = false

    /**
     * 作用域
     */
    private val activityStackScope = ActivityStackScopeImpl()

    /**
     * 这个栈会保存所有的 Activity 实例
     */
    private val activityStack: Stack<Activity> = Stack()

    /**
     * Activity 创建的事件
     */
    private val _activityCreateEvent = CacheSharedFlow<Activity>()
    val activityCreateEvent: Flow<Activity> = _activityCreateEvent

    /**
     * Activity 销毁的事件
     */
    private val _activityDestroyEvent = CacheSharedFlow<Activity>()
    val activityDestroyEvent: Flow<Activity> = _activityDestroyEvent

    private val _lifecycleEvent = CacheSharedFlow<ActivityLifecycleEvent>()
    val lifecycleEvent: Flow<ActivityLifecycleEvent> = _lifecycleEvent

    /**
     * 利用 ActivityStack 实现的是否在后台的状态 Flow
     * 内部默认 1s 的防抖动处理
     */
    @OptIn(FlowPreview::class)
    val isRunningInBackgroundStateOb = lifecycleEvent
        .map { it.event }
        .filter {
            it == Lifecycle.Event.ON_RESUME || it == Lifecycle.Event.ON_PAUSE
        }
        .map {
            it == Lifecycle.Event.ON_PAUSE
        }
        .debounce(timeoutMillis = 1000)
        .sharedStateIn(
            initValue = false,
            scope = AppScope,
            distinctUntilChanged = true,
        )

    /**
     * 启动的时候就是 Empty 的情况不会有事件
     */
    private val _emptyStackEvent = CacheSharedFlow<Unit>()
    val emptyStackEvent: Flow<Unit> = _emptyStackEvent

    /**
     * @return whether the the size of stack of Activity is zero or not
     */
    val isEmpty: Boolean
        @Synchronized
        get() = activityStack.isEmpty()

    /**
     * 返回顶层的活着的 Activity
     */
    val topAlive: Activity?
        @Synchronized
        get() = first {
            it.isAlive()
        }

    /**
     * 初始化
     */
    fun init(app: Application, isDebug: Boolean = false) {
        this.isDebug = isDebug
        app.registerActivityLifecycleCallbacks(
            ActivityLifecycleCallback()
        )
    }

    /**
     * 结束 [Activity] 通过过滤条件
     */
    @Synchronized
    fun finish(
        condition: ActivityStackScope.(act: Activity) -> Boolean = {
            false
        },
    ) {
        activityStack
            .indices
            .reversed()
            .filter { index ->
                activityStackScope.condition(
                    activityStack[index]
                )
            }.forEach { index ->
                activityStack.removeAt(index)?.run {
                    Log.d(
                        TAG, "finish Activity by condition: $this"
                    )
                    this.finish()
                }
            }
    }

    /**
     * 获取 [Activity] 通过过滤条件
     */
    @Synchronized
    fun get(
        condition: ActivityStackScope.(act: Activity) -> Boolean = {
            true
        },
    ): List<Activity> {
        return activityStack
            .reversed()
            .filter { act ->
                activityStackScope.condition(
                    act
                )
            }.apply {
                Log.d(
                    TAG, "get Activity by condition: $this"
                )
            }
    }

    /**
     * 获取 [Activity] 通过过滤条件
     */
    @Synchronized
    fun first(
        condition: ActivityStackScope.(act: Activity) -> Boolean = {
            true
        },
    ): Activity? {
        return activityStack
            .reversed()
            .firstOrNull { act ->
                activityStackScope.condition(
                    act
                )
            }.apply {
                Log.d(
                    TAG, "get first Activity by condition: $this"
                )
            }
    }

    /**
     * 是否有 [Activity] 通过过滤条件
     */
    @Synchronized
    fun any(
        condition: ActivityStackScope.(act: Activity) -> Boolean,
    ): Boolean {
        return activityStack
            .any { act ->
                activityStackScope.condition(
                    act
                )
            }
            .apply {
                Log.d(
                    TAG, "match any Activity by condition: $this"
                )
            }
    }

    @Synchronized
    internal fun pushActivity(activity: Activity) {
        Log.d(
            TAG, "pushActivity: $activity"
        )
        if (activityStack.contains(activity)) {
            return
        }
        _activityCreateEvent.add(
            value = activity
        )
        activityStack.add(activity)
    }

    @Synchronized
    internal fun removeActivity(activity: Activity) {
        Log.d(
            TAG, "removeActivity: $activity"
        )
        activityStack.remove(activity)
        _activityDestroyEvent.add(
            value = activity
        )
        if (activityStack.isEmpty()) {
            _emptyStackEvent.add(
                value = Unit
            )
        }
    }

    @Synchronized
    internal fun pushLifecycleEvent(
        activity: Activity,
        event: Lifecycle.Event,
    ) {
        _lifecycleEvent.add(
            value = ActivityLifecycleEvent(
                activity = activity,
                event = event,
            )
        )
    }

    /**
     * 对 Flow<Activity> 的扩展
     */
    fun Flow<Activity>.filterActivity(
        condition: ActivityStackScope.(act: Activity) -> Boolean
    ): Flow<Activity> {
        return this.filter { act ->
            with(receiver = activityStackScope) {
                this.condition(act)
            }
        }
    }

}
