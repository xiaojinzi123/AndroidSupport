package com.xiaojinzi.support.ktx

import android.app.Application
import android.content.Context
import android.view.View
import androidx.annotation.StringRes
import com.xiaojinzi.support.bean.StringItemDto
import com.xiaojinzi.support.init.AppInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.UUID
import java.util.concurrent.atomic.AtomicInteger

/**
 * 全局可用的 Application
 */
val app: Application
    get() = AppInstance.app

/**
 * 全局使用的 Scope, 就不要加那个注解. 不然会报黄
 */
val AppScope: CoroutineScope get() = GlobalScope

/**
 * 创建一个 IO 线程的协程 Scope
 */
fun IOScope(): CoroutineScope = CoroutineScope(context = Dispatchers.IO)

/**
 * 创建一个 UUID
 */
fun newUUid() = UUID.randomUUID().toString()

private val snowflakeIdGenerator = SnowflakeIdGenerator()

/**
 * 创建一个雪花 Id
 */
fun newSnowflakeId(): Long {
    return snowflakeIdGenerator.nextId()
}

fun Boolean.toVisible(isGone: Boolean = false) =
    if (this) View.VISIBLE else (if (isGone) View.GONE else View.INVISIBLE)

/**
 * 为了有些时候的链式操作, 最后的逗号用此方法搞一哈
 */
fun <T> T.nothing(): T {
    return this
}

fun notSupportError(message: String = "Not Support"): Nothing = error(message = message)

private val counter = AtomicInteger()
fun generateUniqueStr(): String {
    return newUUid() + counter.incrementAndGet()
}

fun StringItemDto.contentWithContext(context: Context = app): String {
    return value ?: context.getString(valueRsd!!)
}

fun @receiver:StringRes Int.toStringItemDto() = StringItemDto(valueRsd = this)

fun String.toStringItemDto() = StringItemDto(value = this)

@Deprecated(
    message = "rename to toStringItemDto",
    replaceWith = ReplaceWith(
        expression = "toStringItemDto()",
        imports = ["com.xiaojinzi.support.ktx.toStringItemDto"]
    ),
    level = DeprecationLevel.WARNING,
)
fun @receiver:StringRes Int.toStringItemDTO() = this.toStringItemDto()

@Deprecated(
    message = "rename to toStringItemDto",
    replaceWith = ReplaceWith(
        expression = "toStringItemDto()",
        imports = ["com.xiaojinzi.support.ktx.toStringItemDto"]
    ),
    level = DeprecationLevel.WARNING,
)
fun String.toStringItemDTO() = StringItemDto(value = this)

/**
 * 在协程中执行一个任务, 并且忽略错误
 */
fun executeTaskInCoroutinesIgnoreError(
    scope: CoroutineScope = AppScope,
    task: suspend () -> Unit,
) {
    scope.launch(context = ErrorIgnoreContext) {
        task.invoke()
    }
}