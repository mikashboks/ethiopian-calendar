package com.mkb.ethiopian.lib

import android.content.Context
import android.graphics.PorterDuff
import android.util.TypedValue
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import java.util.*

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