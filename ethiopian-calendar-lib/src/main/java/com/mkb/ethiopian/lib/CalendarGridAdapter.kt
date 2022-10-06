package com.mkb.ethiopian.lib


import android.content.Context
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.mkb.ethiopian.lib.models.DayAndDates
import java.util.*


class CalendarGridAdapter(
    private val mContext: Context, private var monthlyDates: List<Date>, // first Day of the month
    private val calendarPrimaryColor: Int, private var firstDayofTheMonthDate: Date
) : ArrayAdapter<Date>(mContext, R.layout.cell_layout) {

    var selectedDate: Date? = null

    private val mInflater: LayoutInflater = LayoutInflater.from(mContext)

    fun setDates(list: List<Date>, cal: Date) {
        monthlyDates = list
        firstDayofTheMonthDate = cal
    }

    override fun getCount(): Int {
        return monthlyDates.size
    }

    override fun getItem(position: Int): Date {
        return monthlyDates[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val mTodayDate = getItem(position)

        var view = convertView
        if (view == null) {
            view = mInflater.inflate(R.layout.cell_layout, parent, false)
            view.tag = CellViewHolder(view)
        }
        val cellViewHolder = view!!.tag as CellViewHolder

        val calendarSelected = Calendar.getInstance()
        calendarSelected.time = mTodayDate

        val eCal = EthiopicCalendar(calendarSelected)
        var values: IntArray = eCal.gregorianToEthiopic()
        val dayValueEth = values[2]
        val displayMonthEth = values[1]
        val displayYearEth = values[0]
        calendarSelected.time = firstDayofTheMonthDate

        values = EthiopicCalendar(calendarSelected).gregorianToEthiopic()
        val currentMonth = values[1]
        val currentYear = values[0]

        with(cellViewHolder) {
            todayDateView.isVisible = false
            selectedDateView.isVisible = selectedDate == mTodayDate
            cellDateEth.text = dayValueEth.toString()
            cellDateGreg.text =
                DayAndDates.Months.gMonths.get(calendarSelected[Calendar.MONTH]) + " " + calendarSelected[Calendar.DAY_OF_MONTH]

            calendarSelected.time = mTodayDate
            cellViewHolder.selectedDateView.setColorFilter(calendarPrimaryColor, PorterDuff.Mode.SRC_IN)

            if (Calendar.getInstance()[Calendar.DAY_OF_MONTH] == calendarSelected[Calendar.DAY_OF_MONTH] &&
                Calendar.getInstance()[Calendar.YEAR] == calendarSelected[Calendar.YEAR] &&
                Calendar.getInstance()[Calendar.MONTH] == calendarSelected[Calendar.MONTH]
            ) {

                if(selectedDate == null) selectedDate = mTodayDate
                todayDateView.isVisible = true

                if (selectedDate == mTodayDate) {
                    cellDateEth.setTextColor(
                        ContextCompat.getColor(mContext, R.color.colorWhite)
                    )
                    cellDateGreg.setTextColor(
                        ContextCompat.getColor(mContext, R.color.colorWhite)
                    )
                } else {
                    cellDateEth.setTextColor(calendarPrimaryColor)
                    cellDateGreg.setTextColor(calendarPrimaryColor)
                }

            } else if (selectedDate == mTodayDate) {
                cellDateEth.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite))
                cellDateGreg.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite))
            } else if (displayMonthEth == currentMonth && displayYearEth == currentYear) {
                cellDateEth.setTextColor(ContextCompat.getColor(mContext, R.color.GreyBlue))
                cellDateGreg.setTextColor(ContextCompat.getColor(mContext, R.color.GreyBlue))
            } else {
                cellDateEth.setTextColor(ContextCompat.getColor(mContext, R.color.LightGrey))
                cellDateGreg.setTextColor(ContextCompat.getColor(mContext, R.color.LightGrey))
            }
        }

        view.setOnClickListener {
            if (displayMonthEth == currentMonth && displayYearEth == currentYear) {
                selectedDate = mTodayDate
                Toast.makeText(mContext, "Selected: $selectedDate", Toast.LENGTH_SHORT).show()
                notifyDataSetChanged()
            }
        }

        return view
    }

    private class CellViewHolder(view: View) {

        var selectedDateView: ImageView
        var todayDateView: ImageView
        var cellDateEth: TextView
        var cellDateGreg: TextView

        init {
            selectedDateView = view.findViewById(R.id.selected_date)
            todayDateView = view.findViewById(R.id.today_date)
            cellDateEth = view.findViewById(R.id.calendar_date_id)
            cellDateGreg = view.findViewById(R.id.calendar_date_id_greg)
        }
    }
}