package com.mkb.ethiopian.lib

import android.content.Context
import android.util.TypedValue
import androidx.annotation.ColorInt

fun Context.getColorFromContext(@ColorInt resId: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(resId, typedValue, true)
    return typedValue.data
}