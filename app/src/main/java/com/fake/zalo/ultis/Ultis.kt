package com.fake.zalo.ultis

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.View
import android.view.WindowInsets

@SuppressLint("InternalInsetResource")
fun getNavigationBarHeight(context: Context, view: View): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val insets = view.rootWindowInsets
        insets?.getInsets(WindowInsets.Type.navigationBars())?.bottom ?: 0
    } else {
        val resourceId = context.resources.getIdentifier("navigation_bar_height", "dimen", "android")
        if (resourceId > 0) context.resources.getDimensionPixelSize(resourceId) else 0
    }
}