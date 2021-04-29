package com.xiaojinzi.support.ktx

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

enum class Mode {
  Inflate, Bind
}

@MainThread
inline fun <reified VB : ViewBinding> View.viewBindings() = lazy { inflateViewBinding<VB>(inflater = context.inflater) }

@MainThread
inline fun <reified VB : ViewBinding> Dialog.viewBindings() = lazy { inflateViewBinding<VB>(inflater = context.inflater) }

@MainThread
inline fun <reified VB : ViewBinding> Context.viewBindings() = lazy { inflateViewBinding<VB>(inflater = inflater) }

@MainThread
inline fun <reified VB : ViewBinding> Activity.viewBindings() = lazy {
  inflateViewBinding<VB>(inflater = inflater)
}

@MainThread
inline fun <reified VB : ViewBinding> Fragment.viewBindings(mode: Mode = Mode.Inflate) = lazy {
  when (mode) {
    Mode.Inflate -> inflateViewBinding<VB>(inflater = requireContext().inflater)
    Mode.Bind -> bindViewBinding(requireView())
  }
}

@MainThread
inline fun <reified VB : ViewBinding> inflateViewBinding(inflater: LayoutInflater): VB {
  return VB::class.java.getMethod("inflate", LayoutInflater::class.java)
      .invoke(null, inflater) as VB
}

@MainThread
inline fun <reified VB : ViewBinding> bindViewBinding(view: View): VB {
  return VB::class.java.getMethod("bind", View::class.java)
      .invoke(null, view) as VB
}
