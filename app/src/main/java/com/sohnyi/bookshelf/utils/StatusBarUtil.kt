package com.sohnyi.bookshelf.utils

import android.app.Activity
import android.view.View
import android.view.Window
import androidx.annotation.ColorInt


/**
 * Set the status bar's light mode.
 *
 * @param isDarkText True to set status bar light mode, false otherwise.
 */
fun Activity.setStatusBarLightMode(isDarkText: Boolean) {
    this.window.setStatusBarLightMode(isDarkText)
}

fun Activity.setStatusBarMode(@ColorInt barColor: Int, isDarkText: Boolean) {
    this.window.setStatusBarMode(barColor, isDarkText)
}

/**
 *
 */
fun Window.setStatusBarLightMode(isDarkText: Boolean) {
    val decorView: View = this.decorView
    var vis = decorView.systemUiVisibility
    vis = if (isDarkText) {
        vis or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    } else {
        vis and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
    }
    decorView.systemUiVisibility = vis
}


fun Window.setStatusBarMode(@ColorInt barColor: Int, isDarkText: Boolean) {
    this.statusBarColor = barColor
    this.setStatusBarLightMode(isDarkText)
}