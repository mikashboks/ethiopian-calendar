package com.mkb.ethiopian.lib

import android.content.Context
import android.graphics.PorterDuff
import android.util.TypedValue
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import java.util.Calendar

fun Context.getColorFromContext(@ColorInt resId: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(resId, typedValue, true)
    return typedValue.data
}

fun getEtCalendar(values: IntArray): Calendar {
    return Calendar.getInstance().apply {
        set(values[0], values[1], values[2], 0, 0, 0)
        set(Calendar.MILLISECOND, 0)
    }
}

fun ImageView.enableView(enable: Boolean) {
    isEnabled = enable
    setColorFilter(
        ContextCompat.getColor(
            context,
            if (enable) R.color.colorWhite else R.color.white_50
        ), PorterDuff.Mode.SRC_IN
    )
}

fun removeTime(day: Int, month: Int, year: Int): Calendar {
    return removeTime(Calendar.getInstance().apply {
        set(year, month, day)
    })
}

fun removeTime(calendar: Calendar): Calendar {
    return Calendar.getInstance().apply {
        timeInMillis = calendar.timeInMillis
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
}

fun Calendar.isEqualDate(date: Long?): Boolean {
    if (date == null) return false
    val dateCal = Calendar.getInstance().apply {
        timeInMillis = date
    }
    return get(Calendar.DAY_OF_MONTH) == dateCal[Calendar.DAY_OF_MONTH] &&
            get(Calendar.MONTH) == dateCal[Calendar.MONTH] &&
            get(Calendar.YEAR) == dateCal[Calendar.YEAR]
}

fun Calendar.isGreaterThanOrEqual(date: Long?): Boolean {
    if (date == null) return false
    return timeInMillis > date || isEqualDate(date)
}

fun Calendar.isLessThanOrEqual(date: Long?): Boolean {
    if (date == null) return false
    return timeInMillis < date || isEqualDate(date)
}
