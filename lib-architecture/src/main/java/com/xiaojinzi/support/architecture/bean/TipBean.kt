package com.xiaojinzi.support.architecture.bean

import androidx.annotation.Keep

enum class TipType {
  Toast, Dialog,
}

@Keep
data class TipBean(val type: TipType, val content: String)