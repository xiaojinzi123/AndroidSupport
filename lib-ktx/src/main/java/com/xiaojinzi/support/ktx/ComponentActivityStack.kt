package com.xiaojinzi.support.ktx

import android.app.Activity
import android.os.Build
import java.util.*

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

/**
 * Component 的 Activity 栈
 *
 * @author xiaojinzi
 */
object ComponentActivityStack {

    /**
     * the stack will be save all reference of Activity
     */
    private val activityStack: Stack<Activity> = Stack()

    /**
     * 进入栈
     */
    @Synchronized
    fun pushActivity(activity: Activity) {
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
    fun removeActivity(activity: Activity) {
        activityStack.remove(activity)
    }

    /**
     * 是否存在某个标记的界面
     */
    @Synchronized
    fun isActivityExistByFlag(flag: String): Boolean {
        return activityStack
            .find { act ->
                act.javaClass.getAnnotation(ActivityFlag::class.java)?.value?.contains(element = flag)?: false
            } != null
    }

    /**
     * 杀掉所有界面
     */
    @Synchronized
    fun finishAllActivity() {
        activityStack
            .indices
            .reversed()
            .forEach { index ->
                activityStack.removeAt(index)?.run {
                    this.finish()
                }
            }
    }

    /**
     * Activity 上有注解标记
     * 根据标记删除集合中的 Activity
     */
    @Synchronized
    fun finishActivityWithFlag(flag: String) {
        activityStack
            .indices
            .reversed()
            .filter { index ->
                activityStack[index].javaClass.getAnnotation(ActivityFlag::class.java)?.value?.contains(element = flag)?: false
            }.forEach { index ->
                activityStack.removeAt(index)?.run {
                    this.finish()
                }
            }
    }

    /**
     * Activity 上有注解标记
     * 删除集合中不含有指定标记的界面
     */
    @Synchronized
    fun finishActivityExcludeFlag(flag: String) {
        activityStack
            .indices
            .reversed()
            .filter { index ->
                (activityStack[index].javaClass.getAnnotation(ActivityFlag::class.java)?.value?.contains(element = flag)?: false).not()
            }.forEach { index ->
                activityStack.removeAt(index)?.run {
                    this.finish()
                }
            }
    }
    @Synchronized
    fun finishByClassName(className: String) {
        activityStack
            .indices
            .reversed()
            .filter { index ->
                activityStack[index].javaClass.name == className
            }.forEach { index ->
                activityStack.removeAt(index)?.run {
                    this.finish()
                }
            }
    }

    /**
     * @return whether the the size of stack of Activity is zero or not
     */
    @Synchronized
    fun isEmpty(): Boolean {
        return activityStack.isEmpty()
    }

    /**
     * 返回顶层的 Activity
     */
    @Synchronized
    fun getTopActivity(): Activity? {
        return if (isEmpty()) null else activityStack[activityStack.lastIndex]// 如果已经销毁, 就下一个
    }

    /**
     * 返回顶层的活着的 Activity
     */
    @Synchronized
    fun getTopAliveActivity(): Activity? {
        var result: Activity? = null
        if (!isEmpty()) {
            val size = activityStack.size
            for (i in size - 1 downTo 0) {
                val activity = activityStack[i]
                // 如果已经销毁, 就下一个
                if (!isActivityDestroy(activity)) {
                    result = activity
                    break
                }
            }
        }
        return result
    }

    /**
     * 返回顶层的 Activity除了某一个
     */
    @Synchronized
    fun getTopActivityExcept(clazz: Class<out Activity?>): Activity? {
        val size = activityStack.size
        for (i in size - 1 downTo 0) {
            val itemActivity = activityStack[i]
            if (itemActivity.javaClass != clazz) {
                return itemActivity
            }
        }
        return null
    }

    /**
     * 返回顶层的第二个 Activity
     */
    @Synchronized
    fun getSecondTopActivity(): Activity? {
        return if (isEmpty() || activityStack.size < 2) null else activityStack[activityStack.lastIndex - 1]
    }

    /**
     * 返回底层的 Activity
     */
    @Synchronized
    fun getBottomActivity(): Activity? {
        return if (isEmpty() || activityStack.size < 1) null else activityStack[0]
    }

    /**
     * 是否存在某一个 Activity
     */
    @Synchronized
    fun isExistActivity(clazz: Class<out Activity>): Boolean {
        for (activity in activityStack) {
            if (activity.javaClass == clazz) {
                return true
            }
        }
        return false
    }

    @Synchronized
    fun isExistOtherActivityExcept(clazz: Class<out Activity>): Boolean {
        for (activity in activityStack) {
            if (activity.javaClass != clazz) {
                return true
            }
        }
        return false
    }

    /**
     * Activity 是否被销毁了
     */
    private fun isActivityDestroy(activity: Activity): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            activity.isFinishing || activity.isDestroyed
        } else {
            activity.isFinishing
        }
    }

}