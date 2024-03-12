package com.xiaojinzi.support.ktx

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.TimeZone

/**
 * 执行这个任务最低需要的时间
 */
suspend fun <T> timeAtLeast(timeMillis: Long = 500L, callable: suspend () -> T): T {
    return withContext(context = Dispatchers.IO) {
        val job1 = async {
            delay(timeMillis = timeMillis)
        }
        val job2 = async {
            try {
                callable.invoke()
            } catch (e: Exception) {
                throw e
            } finally {
                job1.await()
            }
        }
        job2.await()
    }
}

/**
 * 获取时间戳里面的小时
 */
fun getHourOfDayByTimeStamp(
    timeStamp: Long,
    zone: TimeZone = TimeZone.getDefault(),
): Int {
    val calendar = Calendar.getInstance(zone)
    calendar.timeInMillis = timeStamp
    return calendar.get(Calendar.HOUR_OF_DAY)
}

/**
 * 获取时间戳里面的小时
 */
fun getHourByTimeStamp(
    timeStamp: Long,
    zone: TimeZone = TimeZone.getDefault(),
): Int {
    val calendar = Calendar.getInstance(zone)
    calendar.timeInMillis = timeStamp
    return calendar.get(Calendar.HOUR)
}

/**
 * 获取时间戳里面的分钟
 */
fun getMinuteByTimeStamp(
    timeStamp: Long,
    zone: TimeZone = TimeZone.getDefault(),
): Int {
    val calendar = Calendar.getInstance(zone)
    calendar.timeInMillis = timeStamp
    return calendar.get(Calendar.MINUTE)
}

/**
 * 获取时间戳里面的秒
 */
fun getSecondByTimeStamp(
    timeStamp: Long,
    zone: TimeZone = TimeZone.getDefault(),
): Int {
    val calendar = Calendar.getInstance(zone)
    calendar.timeInMillis = timeStamp
    return calendar.get(Calendar.SECOND)
}

/**
 * 获取是一周的第几天
 * 第一个值是 1, 表示周日
 */
fun getDayOfWeek(
    timeStamp: Long,
    zone: TimeZone = TimeZone.getDefault(),
): Int {
    val calendar = Calendar.getInstance(zone)
    calendar.timeInMillis = timeStamp
    return calendar.get(Calendar.DAY_OF_WEEK)
}

/**
 * 获取月中的第几天
 * 第一天是 1
 */
fun getDayOfMonth(
    timeStamp: Long,
    zone: TimeZone = TimeZone.getDefault(),
): Int {
    val calendar = Calendar.getInstance(zone)
    calendar.timeInMillis = timeStamp
    return calendar.get(Calendar.DAY_OF_MONTH)
}

/**
 * 获取年中的第几天
 * 第一天是 1
 */
fun getDayOfYear(
    timeStamp: Long,
    zone: TimeZone = TimeZone.getDefault(),
): Int {
    val calendar = Calendar.getInstance(zone)
    calendar.timeInMillis = timeStamp
    return calendar.get(Calendar.DAY_OF_YEAR)
}

/**
 * 获取是第几个月, 第一个月是 0
 */
fun getMonthByTimeStamp(
    timeStamp: Long,
    zone: TimeZone = TimeZone.getDefault(),
): Int {
    val calendar = Calendar.getInstance(zone)
    calendar.timeInMillis = timeStamp
    return calendar.get(Calendar.MONTH)
}

/**
 * 获取是第几年
 */
fun getYearByTimeStamp(
    timeStamp: Long,
    zone: TimeZone = TimeZone.getDefault(),
): Int {
    val calendar = Calendar.getInstance(zone)
    calendar.timeInMillis = timeStamp
    return calendar.get(Calendar.YEAR)
}

/**
 * 获取此时间戳的当天的起始时间
 */
fun getDayInterval(
    timeStamp: Long,
    zone: TimeZone = TimeZone.getDefault(),
): Pair<Long, Long> {
    val calendar = Calendar.getInstance(zone)
    calendar.timeInMillis = timeStamp
    calendar[Calendar.HOUR_OF_DAY] = 0
    calendar[Calendar.SECOND] = 0
    calendar[Calendar.MINUTE] = 0
    calendar[Calendar.MILLISECOND] = 0
    val start = calendar.timeInMillis
    calendar.add(Calendar.DAY_OF_MONTH, 1)
    calendar.add(Calendar.MILLISECOND, -1)
    val end = calendar.timeInMillis
    return Pair(
        first = start,
        second = end,
    )
}

fun isSameDay(
    timeStamp1: Long,
    timeStamp2: Long,
    zone: TimeZone = TimeZone.getDefault(),
) =
    getDayInterval(
        timeStamp = timeStamp1,
        zone = zone,
    ) == getDayInterval(
        timeStamp = timeStamp2,
        zone = zone,
    )


/**
 * 获取此时间戳的当月的起始时间. 左右都是包含的
 */
fun getMonthInterval(
    timeStamp: Long,
    monthOffset: Int = 0,
    zone: TimeZone = TimeZone.getDefault(),
): Pair<Long, Long> {
    val calendar = Calendar.getInstance(zone)
    calendar.timeInMillis = timeStamp
    calendar[Calendar.DAY_OF_MONTH] = 1
    calendar[Calendar.HOUR_OF_DAY] = 0
    calendar[Calendar.MINUTE] = 0
    calendar[Calendar.SECOND] = 0
    calendar[Calendar.MILLISECOND] = 0
    calendar.add(Calendar.MONTH, monthOffset)
    val start = calendar.timeInMillis
    // 月份调整到下个月, 然后毫秒值 -1, 这样就定位到这个月的最后一毫秒了
    calendar.add(Calendar.MONTH, 1)
    calendar.add(Calendar.MILLISECOND, -1)
    val end = calendar.timeInMillis
    return Pair(
        first = start,
        second = end,
    )
}

/**
 * 获取这个时间戳这个月的开始时间
 */
fun getMonthStartTime(
    timeStamp: Long,
    monthOffset: Int = 0,
    zone: TimeZone = TimeZone.getDefault(),
) =
    getMonthInterval(
        timeStamp = timeStamp,
        monthOffset = monthOffset,
        zone = zone,
    ).first

/**
 * 获取此时间戳的当月的起始时间.
 */
fun getYearInterval(
    timeStamp: Long,
    yearOffset: Int = 0,
    zone: TimeZone = TimeZone.getDefault(),
): Pair<Long, Long> {
    val calendar = Calendar.getInstance(zone)
    calendar.timeInMillis = timeStamp
    // 这个不用了, 多余了, 因为下面的 DAY_OF_YEAR 会设置到一年的第一天
    // calendar[Calendar.MONTH] = 0
    calendar[Calendar.DAY_OF_YEAR] = 1
    calendar[Calendar.HOUR_OF_DAY] = 0
    calendar[Calendar.MINUTE] = 0
    calendar[Calendar.SECOND] = 0
    calendar[Calendar.MILLISECOND] = 0
    calendar.add(Calendar.YEAR, yearOffset)
    val start = calendar.timeInMillis
    // 月份调整到下个月, 然后毫秒值 -1, 这样就定位到这个月的最后一毫秒了
    calendar.add(Calendar.YEAR, 1)
    calendar.add(Calendar.MILLISECOND, -1)
    val end = calendar.timeInMillis
    return Pair(
        first = start,
        second = end,
    )
}

/**
 * 获取这个时间戳这个年的开始时间
 */
fun getYearStartTime(
    timeStamp: Long,
    yearOffset: Int = 0,
    zone: TimeZone = TimeZone.getDefault(),
) =
    getYearInterval(
        timeStamp = timeStamp,
        yearOffset = yearOffset,
        zone = zone,
    ).first