package com.xiaojinzi.support.bean

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.annotation.StringRes
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class StringItemDto(
    @StringRes
    val valueRsd: Int? = null,
    val value: String? = null,
): Parcelable {

    fun isEmpty(): Boolean {
        return valueRsd == null && value.isNullOrEmpty()
    }

    init {
        if(valueRsd == null && value.isNullOrEmpty()) {
            throw IllegalArgumentException("valueRsd and value can not be null at the same time")
        }
    }

}

typealias StringItemDTO = StringItemDto