package com.xiaojinzi.support.ktx

import android.app.Activity
import android.app.DownloadManager
import android.app.KeyguardManager
import android.app.NotificationManager
import android.bluetooth.BluetoothManager
import android.content.ClipboardManager
import android.content.Context
import android.content.ContextWrapper
import android.hardware.usb.UsbManager
import android.location.LocationManager
import android.nfc.NfcManager
import android.os.BatteryManager
import android.os.Build
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.CaptioningManager
import android.view.inputmethod.InputMethodManager
import androidx.annotation.LayoutRes
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity

/**
 * 从 Context 获取 FragmentActivity
 */
fun Context.getFragmentActivity(): FragmentActivity? {
    val targetActivity = this.getActivity()
    return if (targetActivity is FragmentActivity) {
        targetActivity
    } else {
        null
    }
}

/**
 * 从 Context 获取 Activity
 */
fun Context.getActivity(): Activity? {
    var realActivity: Activity? = null
    if (this is Activity) {
        realActivity = this
    } else {
        // 最终结束的条件是 realContext = null 或者 realContext 不是一个 ContextWrapper
        var realContext: Context? = this
        while (realContext is ContextWrapper) {
            realContext = realContext.baseContext
            if (realContext is Activity) {
                realActivity = realContext
                break
            }
        }
    }
    return realActivity
}

/**
 * 获取 LayoutInflater
 */
val Context.inflater: LayoutInflater
    get() = LayoutInflater.from(this)

/**
 * 创建视图
 */
fun Context.inflateLayout(
    @LayoutRes layoutId: Int,
    parent: ViewGroup? = null,
    attachToRoot: Boolean = false
): View = inflater.inflate(layoutId, parent, attachToRoot)

val Context.vibrator: Vibrator?
    get() = getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
val Context.bluetoothManager: BluetoothManager?
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    get() = getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
val Context.batteryManager: BatteryManager?
    get() = getSystemService(Context.BATTERY_SERVICE) as? BatteryManager
val Context.downloadManager: DownloadManager?
    get() = getSystemService(Context.DOWNLOAD_SERVICE) as? DownloadManager
val Context.clipboardManager: ClipboardManager?
    get() = getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
val Context.inputMethodManager: InputMethodManager?
    get() = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
val Context.notificationManager: NotificationManager?
    get() = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
val Context.keyguardManager: KeyguardManager?
    get() = getSystemService(Context.KEYGUARD_SERVICE) as? KeyguardManager
val Context.locationManager: LocationManager?
    get() = getSystemService(Context.LOCATION_SERVICE) as? LocationManager
val Context.captioningManager: CaptioningManager?
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    get() = this.getSystemService(Context.CAPTIONING_SERVICE) as? CaptioningManager
val Context.nfcManager: NfcManager?
    get() = this.getSystemService(Context.NFC_SERVICE) as? NfcManager
val Context.usbManager: UsbManager?
    get() = this.getSystemService(Context.USB_SERVICE) as? UsbManager
