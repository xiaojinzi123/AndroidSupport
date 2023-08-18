package com.xiaojinzi.support.activity_stack

import android.app.Activity
import android.app.Application
import android.os.Build
import android.os.Bundle
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

interface ActivityStackScope {

    /**
     * 还活着的
     */
    fun Activity.isAlive(): Boolean

    /**
     * 是否有某个 flag
     */
    fun Activity.hasFlag(flag: String): Boolean

    /**
     * 有其中的任意一个 flag
     */
    fun Activity.anyFlag(vararg args: String): Boolean

    /**
     * 得包含所有的 flag
     */
    fun Activity.allFlag(vararg args: String): Boolean

}

private class ActivityStackScopeImpl : ActivityStackScope {

    override fun Activity.isAlive(): Boolean =
        !if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            this.isFinishing || this.isDestroyed
        } else {
            this.isFinishing
        }

    override fun Activity.hasFlag(flag: String) = this
        .javaClass
        .getAnnotation(ActivityFlag::class.java)
        ?.value
        ?.any {
            it == flag
        } ?: false

    override fun Activity.anyFlag(vararg args: String) = this
        .javaClass
        .getAnnotation(ActivityFlag::class.java)
        ?.value
        ?.any {
            it in args
        } ?: false

    override fun Activity.allFlag(vararg args: String): Boolean {
        val activityFlags = this
            .javaClass
            .getAnnotation(ActivityFlag::class.java)
            ?.value ?: emptyArray()
        return args.all {
            it in activityFlags
        }
    }

}

private class ActivityLifecycleCallback : Application.ActivityLifecycleCallbacks {

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        ActivityStack.pushActivity(activity)
    }

    override fun onActivityStarted(activity: Activity) {
        // ignore
    }

    override fun onActivityResumed(activity: Activity) {
        // ignore
    }

    override fun onActivityPaused(activity: Activity) {
        // ignore
    }

    override fun onActivityStopped(activity: Activity) {
        // ignore
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        // ignore
    }

    override fun onActivityDestroyed(activity: Activity) {
        ActivityStack.removeActivity(activity)
    }

}

/**
 * Activity 栈
 *
 * @author xiaojinzi
 */
object ActivityStack {

    private val activityStackScope = ActivityStackScopeImpl()

    /**
     * 这个栈会保存所有的 Activity 实例
     */
    private val activityStack: Stack<Activity> = Stack()

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
    fun init(app: Application) {
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
            true
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
            .first { act ->
                activityStackScope.condition(
                    act
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
    }

    /**
     * 进入栈
     */
    @Synchronized
    internal fun pushActivity(activity: Activity) {
        if (activityStack.contains(activity)) {
            return
        }
        activityStack.add(activity)
    }

    /**
     * remove the reference of Activity
     *
     * @author xiaojinzi
     */
    @Synchronized
    internal fun removeActivity(activity: Activity) {
        activityStack.remove(activity)
    }

}