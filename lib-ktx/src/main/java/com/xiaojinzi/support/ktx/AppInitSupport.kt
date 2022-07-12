package com.xiaojinzi.support.ktx

import androidx.annotation.Keep
import com.xiaojinzi.support.util.LogSupport
import java.util.*

/**
 * App 初始化的任务
 */
@Keep
data class AppInitTask(
    // 优先级
    val priority: Int = 0,
    val action: suspend () -> Unit,
): Comparable<AppInitTask> {
    override fun compareTo(other: AppInitTask): Int = other.priority - this.priority
}

object AppInitSupport {

    private val taskList = PriorityQueue<AppInitTask>()

    fun addTask(vararg tasks: AppInitTask): AppInitSupport {
        taskList.addAll(elements = tasks)
        return this
    }

    suspend fun execute() {
        taskList.forEach { task ->
            task.action.invoke()
        }
    }

}