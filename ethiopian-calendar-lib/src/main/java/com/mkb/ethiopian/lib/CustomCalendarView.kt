package com.mkb.ethiopian.lib

import android.content.Context
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
import java.util.*


class CustomCalendarView : LinearLayout {
    private lateinit var mContext: Context
    private val mCalendar: Calendar by lazy { Calendar.getInstance() }
    private var mPreviousBtn: ImageView? = null
    private var mNextBtn: ImageView? = null
    private var mCurrentMonthEC: TextView? = null
    private var mCurrentMonthGC: TextView? = null
    private var mCalendarGridView: GridView? = null
    private var mAdapter: CalendarGridAdapter? = null
    private var dayValueInCells: List<Date> = ArrayList()
    private var mCurrentEthiopianYear = 0
    private var mCurrentEthiopianMonth = 0
    private var mCurrentEthiopianDay = 0
    private var mFirstDayOfTheMonth: Date? = null

    constructor(context: Context?) : super(context) {
        mContext = context!!
        initView(null)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        mContext = context!!
        initView(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        mContext = context!!
        initView(attrs)
    }

    private fun initView(attrs: AttributeSet?) {
        val values: IntArray = EthiopicCalendar(mCalendar).gregorianToEthiopic()
        mCurrentEthiopianYear = values[0]
        mCurrentEthiopianMonth = values[1]
        mCurrentEthiopianDay = values[2]
        dayValueInCells = getListOfDates(mCalendar)
        bindViews(attrs)
        val calendarPrimaryColor =
            mContext.theme.obtainStyledAttributes(attrs, R.styleable.CustomCalendarView, 0, 0)
                .getColor(
                    R.styleable.CustomCalendarView_calendarPrimaryColor,
                    ContextCompat.getColor(context, R.color.colorPrimary)
                )
        mAdapter = CalendarGridAdapter(
            mContext,
            dayValueInCells,
            calendarPrimaryColor,
            mFirstDayOfTheMonth!!
        )
        mCalendarGridView!!.adapter = mAdapter
        mCalendarGridView!!.horizontalSpacing = 4
        mCalendarGridView!!.verticalSpacing = 4
    }

    private fun bindViews(attrs: AttributeSet?) {
        val inflater =
            mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = inflater.inflate(R.layout.calendar_view, this)

        mContext.theme.obtainStyledAttributes(attrs, R.styleable.CustomCalendarView, 0, 0).apply {
            val calendarPrimaryColor =
                getColor(
                    R.styleable.CustomCalendarView_calendarPrimaryColor,
                    ContextCompat.getColor(context, R.color.colorPrimary)
                )
            view.findViewById<View>(R.id.header_title).setBackgroundColor(calendarPrimaryColor)
            view.findViewById<View>(R.id.subheader).setBackgroundColor(calendarPrimaryColor)
        }

        mPreviousBtn = view.findViewById(R.id.previous_month_button)
        mNextBtn = view.findViewById(R.id.next_month_button)
        mCurrentMonthEC = view.findViewById(R.id.month_display_EC)
        mCurrentMonthGC = view.findViewById(R.id.moth_display_GC)
        setLabel()
        mCalendarGridView = view.findViewById(R.id.calendar_grid)
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


//        mCalendarGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
////                Toast.makeText(mContext, "Clicked " + position, Toast.LENGTH_LONG).show();
//            }
//        });
    }

    private fun changeMonth() {
        val list = getListOfDates(mCalendar)
        mAdapter?.setDates(list, mFirstDayOfTheMonth!!)
        mAdapter?.notifyDataSetChanged()
        setLabel()
    }

    private fun setLabel() {
        val currentMonthECD =
            mContext.getString(DayAndDates.Months.ethMonths.get(mCurrentEthiopianMonth - 1)) + " " + mCurrentEthiopianYear
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
