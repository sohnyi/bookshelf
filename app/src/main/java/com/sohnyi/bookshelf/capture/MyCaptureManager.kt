package com.sohnyi.bookshelf.capture

import android.app.Activity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.DecoratedBarcodeView

/**
 *
 * Create by yi on Fri 2022/09/23
 * 自定义条形码捕获器
 * 监听生命周期执行生命周期动作
 */
class MyCaptureManager(activity: Activity, barcodeView: DecoratedBarcodeView) :
    CaptureManager(activity, barcodeView), DefaultLifecycleObserver {


    override fun onResume(owner: LifecycleOwner) {
        onResume()
    }

    override fun onPause(owner: LifecycleOwner) {
        onPause()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        onDestroy()
    }
}