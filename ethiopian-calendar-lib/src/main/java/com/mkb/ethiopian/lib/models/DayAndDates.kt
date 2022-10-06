package com.mkb.ethiopian.lib.models

import com.mkb.ethiopian.lib.R

class DayAndDates {
    object Months {
        var ethMonths = intArrayOf(
            R.string.month1, R.string.month2, R.string.month3, R.string.month4, R.string.month5,
            R.string.month6, R.string.month7, R.string.month8, R.string.month9, R.string.month10,
            R.string.month11, R.string.month12, R.string.month13
        )
        var gMonths = arrayOf(
            "Jan",
            "Feb",
            "MAr",
            "Apr",
            "May",
            "Jun",
            "Jul",
            "Aug",
            "Sep",
            "Oct",
            "Nov",
            "Dec",
            "ጳጉሜ"
        )
    }

    object DaysOfWeek {
        var ethDays = intArrayOf(
            R.string.mon, R.string.tue, R.string.wed, R.string.thu, R.string.fri,
            R.string.sat, R.string.sun
        )
        var gregDays = arrayOf(
            "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"
        )
    }
}