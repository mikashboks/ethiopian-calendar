package com.mkb.ethiopian.lib


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.FragmentManager
import java.text.SimpleDateFormat
import java.util.*


class CalendarGridAdapter(
    private val mContext: Context, private var monthlyDates: List<Date>, // first Day of the month
    private var firstDayofTheMonthDate: Date
) : ArrayAdapter<Any?>(mContext, R.layout.cell_layout) {

    private val mInflater: LayoutInflater = LayoutInflater.from(mContext)
    private var mFragmentManager: FragmentManager? = null

    fun setDates(list: List<Date>, cal: Date) {
        monthlyDates = list
        firstDayofTheMonthDate = cal
    }

    override fun getCount(): Int {
        return monthlyDates.size
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getPosition(item: Any?): Int {
//        return monthlyDates.indexOf(item);
        return 0
    }

    @SuppressLint("ResourceAsColor")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val mTodayDate = monthlyDates[position]
        val calendarSelected = Calendar.getInstance()
        calendarSelected.time = mTodayDate
        val eCurrentYear = calendarSelected[Calendar.YEAR]
        val eCurrentMonth = calendarSelected[Calendar.MONTH]
        val eCurrentDate = calendarSelected[Calendar.DAY_OF_MONTH]

//        final String ID= ""+ eCurrentDate + eCurrentMonth + eCurrentYear;
        val eCal = EthiopicCalendar(calendarSelected)
        var values: IntArray = eCal.gregorianToEthiopic()
        val dayValueEth = values[2]
        val displayMonthEth = values[1]
        val displayYearEth = values[0]
        //        Log.d(CAL_TAG ,"month of calendar " + displayMonthEth );

//        dateCal.setTime(firstDayofTheMonthDate);
        calendarSelected.time = firstDayofTheMonthDate
        values = EthiopicCalendar(
            calendarSelected[Calendar.YEAR], calendarSelected[Calendar.MONTH] + 1,
            calendarSelected[Calendar.DAY_OF_MONTH]
        ).gregorianToEthiopic()
        val currentMonth = values[1]
        val currentYear = values[0]
        //        Log.d(CAL_TAG ,"CURRENT MONTH " + currentMonth );
        var view = convertView
        if (view == null) {
            view = mInflater.inflate(R.layout.cell_layout2, parent, false)
        }
        calendarSelected.time = mTodayDate
        val todayIndicator = view!!.findViewById<View>(R.id.today_bookmark) as ImageView
        todayIndicator.setImageDrawable(null)
        val eventIndicator = view.findViewById<View>(R.id.event_indicator) as ImageView
        eventIndicator.setImageDrawable(null)
        val cellDateEth = view.findViewById<View>(R.id.calendar_date_id) as TextView
        cellDateEth.text = dayValueEth.toString()
        val cellDateGreg = view.findViewById<TextView>(R.id.calendar_date_id_greg)
        cellDateGreg.text =
            gMonths[calendarSelected[Calendar.MONTH]] + " " + calendarSelected[Calendar.DAY_OF_MONTH]
        val typedValue = TypedValue()
        if (Calendar.getInstance()[Calendar.DAY_OF_MONTH] == calendarSelected[Calendar.DAY_OF_MONTH] && Calendar.getInstance()[Calendar.YEAR] == calendarSelected[Calendar.YEAR] && Calendar.getInstance()[Calendar.MONTH] == calendarSelected[Calendar.MONTH]) {
            // this is the current day
            mContext.theme.resolveAttribute(R.attr.cellTextColorToday, typedValue, true)
            val color = typedValue.data
            val unwrapped = AppCompatResources.getDrawable(mContext, R.drawable.ic_bookmark_black_24dp)!!
            val wrapped: Drawable = DrawableCompat.wrap(unwrapped)
            DrawableCompat.setTint(wrapped, color)
            todayIndicator.setImageDrawable(wrapped)
            cellDateEth.setTextColor(color)
            cellDateGreg.setTextColor(color)
            //            showEventList(ID , mTodayDate);
        } else if (displayMonthEth == currentMonth && displayYearEth == currentYear) {
            mContext.theme.resolveAttribute(R.attr.cellTextColor, typedValue, true)
            val color = typedValue.data
            cellDateEth.setTextColor(color)
            cellDateGreg.setTextColor(color)
        } else {
            mContext.theme.resolveAttribute(R.attr.cellTextColorOff, typedValue, true)
            val color = typedValue.data
            cellDateEth.setTextColor(color)
            cellDateGreg.setTextColor(color)
        }
        val dateFormater = SimpleDateFormat("ddMMyyyy")
        val dateString = dateFormater.format(mTodayDate)

        // view.setOnClickListener { showEventList(mTodayDate) }

        //Add day to calendar

        //Add events to the calendar
//        TextView eventIndicator = (TextView)view.findViewById(R.id.event_id);
        return view
    }

    private fun showEventList(date: Date) {
        /*val eventListFragment: EventListFragment = EventListFragment.newInstance(date)
        mFragmentManager.beginTransaction()
            .replace(R.id.event_list_fragment_container, eventListFragment)
            .commit()*/
    }

    fun setFM(fragmentManager: FragmentManager?) {
        mFragmentManager = fragmentManager
    }

    companion object {
        private const val CAL_TAG = "THisISWHere"
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
}