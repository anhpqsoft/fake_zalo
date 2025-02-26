package com.fake.zalo.ultis

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.view.View
import android.view.WindowInsets
import androidx.recyclerview.widget.ListAdapter
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

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

@SuppressLint("InternalInsetResource")
fun getStatusBarHeight(activity: Activity): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val windowInsets = activity.window.decorView.rootWindowInsets
        windowInsets?.getInsets(WindowInsets.Type.statusBars())?.top ?: 0
    } else {
        val rect = android.graphics.Rect()
        activity.window.decorView.getWindowVisibleDisplayFrame(rect)
        rect.top
    }
}

const val HH_MM_DD_MM_YYYY = "HH:mm dd/MM/yyyy"
const val HH_MM = "HH:mm"

fun formatTime(timeInMillis: Long, pattern: String = HH_MM): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        DateTimeFormatter.ofPattern(pattern)
            .withZone(ZoneId.systemDefault()).format(Instant.ofEpochMilli(timeInMillis))
    } else {
        formatTimeBeforeSDK26(timeInMillis, pattern)
    }
}

fun formatDateTime(timeInMillis: Long, pattern: String = HH_MM): String {
    if (isToday(timeInMillis)) {
        return formatTime(timeInMillis) + " Hôm nay"
    }
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        DateTimeFormatter.ofPattern(pattern)
            .withZone(ZoneId.systemDefault()).format(Instant.ofEpochMilli(timeInMillis))
    } else {
        formatTimeBeforeSDK26(timeInMillis, pattern)
    }
}

private fun formatTimeBeforeSDK26(timeInMillis: Long, pattern: String = HH_MM): String {
    val sdf = SimpleDateFormat(pattern, Locale.getDefault())
    return sdf.format(Date(timeInMillis))
}

fun isDifferentDay(time1: Long, time2: Long): Boolean {
    val cal1 = Calendar.getInstance().apply { timeInMillis = time1 }
    val cal2 = Calendar.getInstance().apply { timeInMillis = time2 }

    return cal1.get(Calendar.YEAR) != cal2.get(Calendar.YEAR) ||
            cal1.get(Calendar.DAY_OF_YEAR) != cal2.get(Calendar.DAY_OF_YEAR)
}

fun isToday(timeInMillis: Long): Boolean {
    val today = Calendar.getInstance()
    val date = Calendar.getInstance().apply { this.timeInMillis = timeInMillis }

    return today.get(Calendar.YEAR) == date.get(Calendar.YEAR) &&
            today.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR)
}

val Float.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()