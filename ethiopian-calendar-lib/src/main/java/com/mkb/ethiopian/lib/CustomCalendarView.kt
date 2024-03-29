package com.mkb.ethiopian.lib

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.GridView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.mkb.ethiopian.lib.models.DayAndDates
import com.mkb.ethiopian.lib.models.OnSelectListener
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit


class CustomCalendarView : LinearLayout {

    private val mCalendar: Calendar by lazy {
        removeTime(Calendar.getInstance())
    }

    private var mPreviousYearBtn: ImageView? = null
    private var mPreviousBtn: ImageView? = null
    private var mNextBtn: ImageView? = null
    private var mNextYearBtn: ImageView? = null
    private var mCurrentMonthEC: TextView? = null
    private var mCurrentMonthGC: TextView? = null
    private var mCalendarGridView: GridView? = null
    private var mAdapter: CalendarGridAdapter? = null
    private var dayValueInCells: List<Date> = ArrayList()
    private var mCurrentEthiopianYear = 0
    private var mCurrentEthiopianMonth = 0
    private var mCurrentEthiopianDay = 0
    private var mFirstDayOfTheMonth: Date? = null

    private var calendarPrimaryColor: Int = 0

    private lateinit var onSelectListener: OnSelectListener

    /**
     * Minimal long date that user can select
     */
    private var minDate: Long? = null
        set(value) {
            if (value != null && value > 0) {
                // Modify minDate equal to maxDate
                field = if (maxDate != null && value > maxDate!!) {
                    maxDate!!
                } else {
                    value
                }
            }
        }

    /**
     * Maximum long date that user can select
     */
    private var maxDate: Long? = null
        set(value) {
            if (value != null && value > 0) {
                // Modify maxDate equal to minDate
                field = if (minDate != null && value < minDate!!) {
                    minDate!!
                } else {
                    value
                }
            }
        }

    /**
     * By Default selected long date when calendar first time opens
     */
    private var openAt: Long? = null

    constructor(context: Context?) : super(context) {
        initView(null)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initView(attrs)
    }

    private fun loadAdapter() {
        mAdapter = CalendarGridAdapter(
            context, dayValueInCells, calendarPrimaryColor, mFirstDayOfTheMonth!!
        ) {
            if (::onSelectListener.isInitialized) {
                onSelectListener.onDateSelect(it)
            }
        }
        mAdapter!!.selectedDate = openAt
        mAdapter!!.minDate = minDate
        mAdapter!!.maxDate = maxDate
        mCalendarGridView?.adapter = mAdapter
    }

    private fun initView(attrs: AttributeSet?) {
        // Get the calendar primary color
        calendarPrimaryColor =
            context.theme.obtainStyledAttributes(attrs, R.styleable.CustomCalendarView, 0, 0)
                .getColor(
                    R.styleable.CustomCalendarView_calendarPrimaryColor,
                    ContextCompat.getColor(context, R.color.colorPrimary)
                )
    }

    fun buildCalendar(
        openAt: Long? = null, minDate: Long? = null, maxDate: Long? = null,
        primaryColor: Int? = null, onSelectListener: OnSelectListener
    ) {
        this.openAt = openAt
        this.minDate = minDate
        this.maxDate = maxDate
        primaryColor?.let { this.calendarPrimaryColor = it }
        this.onSelectListener = onSelectListener

        if (openAt != null && openAt > 0) {
            mCalendar.timeInMillis = openAt
        }

        val values: IntArray = EthiopicCalendar(mCalendar).gregorianToEthiopic()
        mCurrentEthiopianYear = values[0]
        mCurrentEthiopianMonth = values[1]
        mCurrentEthiopianDay = values[2]
        dayValueInCells = getListOfDates(mCalendar)

        bindViews()
        setCalendarHeaderLabel()
        loadAdapter()

        mCalendarGridView!!.horizontalSpacing =
            context.resources.getDimension(R.dimen.item_spacing).toInt()
        mCalendarGridView!!.verticalSpacing =
            context.resources.getDimension(R.dimen.item_spacing).toInt()

        validateMinDate()
        validateMaxDate()
    }

    private fun setHeaderColors() {
        mainView.findViewById<View>(R.id.header_title).setBackgroundColor(calendarPrimaryColor)
        mainView.findViewById<View>(R.id.subheader).setBackgroundColor(calendarPrimaryColor)
    }

    private val mainView by lazy {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.calendar_view, this)
    }

    private fun bindViews() {

        setHeaderColors()

        mPreviousBtn = mainView.findViewById(R.id.previous_month_button)
        mNextBtn = mainView.findViewById(R.id.next_month_button)
        mPreviousYearBtn = mainView.findViewById(R.id.previous_year_button)
        mNextYearBtn = mainView.findViewById(R.id.next_year_button)

        mCurrentMonthEC = mainView.findViewById(R.id.month_display_EC)
        mCurrentMonthGC = mainView.findViewById(R.id.moth_display_GC)
        setCalendarHeaderLabel()
        mCalendarGridView = mainView.findViewById(R.id.calendar_grid)
        mNextBtn?.setOnClickListener {
            mCurrentEthiopianYear =
                if (mCurrentEthiopianMonth == 13) mCurrentEthiopianYear + 1 else mCurrentEthiopianYear
            mCurrentEthiopianMonth =
                if (mCurrentEthiopianMonth == 13) 1 else mCurrentEthiopianMonth + 1
            changeMonth()
        }
        mPreviousBtn?.setOnClickListener {
            mCurrentEthiopianYear =
                if (mCurrentEthiopianMonth > 1) mCurrentEthiopianYear else mCurrentEthiopianYear - 1
            mCurrentEthiopianMonth =
                if (mCurrentEthiopianMonth > 1) mCurrentEthiopianMonth - 1 else 13
            changeMonth()
        }

        mNextYearBtn?.setOnClickListener {
            mCurrentEthiopianYear += 1
            changeMonth()
        }
        mPreviousYearBtn?.setOnClickListener {
            mCurrentEthiopianYear -= 1
            changeMonth()
        }
    }

    private fun changeMonth() {
        val list = getListOfDates(mCalendar)
        mAdapter?.setDates(list, mFirstDayOfTheMonth!!)
        mAdapter?.notifyDataSetChanged()
        setCalendarHeaderLabel()
        validateMinDate()
        validateMaxDate()
    }

    private fun validateMinDate() {
        if (minDate == null) return
        val minCalendar = Calendar.getInstance().apply {
            timeInMillis = minDate!!
        }
        val minValues: IntArray = EthiopicCalendar(minCalendar).gregorianToEthiopic()
        val prevDay = minValues[2]
        val prevMonth = minValues[1]
        val prevYear = minValues[0]

        val enablePrevMonthArrow = if (prevMonth < mCurrentEthiopianMonth &&
            prevYear <= mCurrentEthiopianYear
        ) true else prevYear < mCurrentEthiopianYear
        mPreviousBtn?.enableView(enablePrevMonthArrow)

        val ecal = EthiopicCalendar(mCurrentEthiopianYear, mCurrentEthiopianMonth, 1)
        val greValues: IntArray = ecal.ethiopicToGregorian()

        mPreviousYearBtn?.enableView(hasAYearDiff(
            minDate!!, getTimeInMills(greValues[2], greValues[1], greValues[0])
        ))
    }

    private fun validateMaxDate() {
        if (maxDate == null) return
        val maxCalendar = Calendar.getInstance().apply {
            timeInMillis = maxDate!!
        }
        val maxValues: IntArray = EthiopicCalendar(maxCalendar).gregorianToEthiopic()
        val nextDay = maxValues[2]
        val nextMonth = maxValues[1]
        val nextYear = maxValues[0]

        val enableNextMonthArrow = if (nextMonth > mCurrentEthiopianMonth &&
            nextYear >= mCurrentEthiopianYear
        ) true else nextYear > mCurrentEthiopianYear
        mNextBtn?.enableView(enableNextMonthArrow)

        val ecal = EthiopicCalendar(mCurrentEthiopianYear, mCurrentEthiopianMonth, 1)
        val greValues: IntArray = ecal.ethiopicToGregorian()
        mNextYearBtn?.enableView(hasAYearDiff(
            getTimeInMills(greValues[2], greValues[1], greValues[0]), maxDate!!
        ))
    }

    private fun setCalendarHeaderLabel() {
        val currentMonthECD =
            context.getString(DayAndDates.Months.ethMonths.get(mCurrentEthiopianMonth - 1)) + " " + mCurrentEthiopianYear
        val cal = Calendar.getInstance()
        cal.time = mFirstDayOfTheMonth!!
        var month = cal[Calendar.MONTH]
        var currentMonthGCD: String =
            DayAndDates.Months.gMonths.get(month) + " " + cal[Calendar.YEAR]
        month = if (month == 11) 0 else month + 1
        currentMonthGCD += " - " + DayAndDates.Months.gMonths.get(month)
            .toString() + " " + cal[Calendar.YEAR]
        mCurrentMonthEC!!.text = currentMonthECD
        mCurrentMonthGC!!.text = currentMonthGCD
    }

    private fun getListOfDates(mCal: Calendar?): List<Date> {

        getFirstDayOfTheMonth()

        // at the end it should return a list of DATES
        val dayValueInCells: MutableList<Date> = ArrayList()

        // check whether we need 5 or 6 rows or for pagume 1 or 3 rows
        val day_of_week = mCal!![Calendar.DAY_OF_WEEK]
        val isSunday = day_of_week == 1
        var MAX_CALENDAR_CELLS = if (isSunday) 42 else 35
        MAX_CALENDAR_CELLS = if (is_pagume) 14 else MAX_CALENDAR_CELLS
        if (mCurrentEthiopianMonth == 13) {
            MAX_CALENDAR_CELLS = 14
        }
        val firstDayOfTheMonthCal = if (isSunday) 6 else day_of_week - 2
        mCal.add(Calendar.DAY_OF_MONTH, -firstDayOfTheMonthCal)
        while (dayValueInCells.size < MAX_CALENDAR_CELLS) {
            dayValueInCells.add(mCal.time)
            mCal.add(Calendar.DAY_OF_MONTH, 1)
        }
        return dayValueInCells
    }

    private fun getFirstDayOfTheMonth() {
        val ecal = EthiopicCalendar(mCurrentEthiopianYear, mCurrentEthiopianMonth, 1)
        val values: IntArray = ecal.ethiopicToGregorian()
        mCalendar[values[0], values[1] - 1] = values[2]
        Log.d(
            THIS_TAG,
            "Current Month GC " + values[1] + " CURRENT MONTH EC " + mCurrentEthiopianMonth
        )
        mFirstDayOfTheMonth = mCalendar.time
    }

    companion object {
        private const val THIS_TAG = "CUSTOM CALENDAR"
        private const val is_pagume = false
    }
}
