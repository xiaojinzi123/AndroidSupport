package com.xiaojinzi.support.bean

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.annotation.StringRes
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class StringItemDTO(
    @StringRes
    val valueRsd: Int? = null,
    val value: String? = null,
): Parcelable {

    fun isEmpty(): Boolean {
        return valueRsd == null && value.isNullOrEmpty()
    }

    init {
        if(valueRsd == null || value == null) {
            throw IllegalArgumentException("nameRsd and name can not be null at the same time")
        }
        "".isEmpty()
    }

}