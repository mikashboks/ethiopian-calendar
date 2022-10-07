package com.mkb.ethiopian.lib

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.mkb.ethiopian.lib.databinding.FragmentCalenderPickerBinding
import com.mkb.ethiopian.lib.models.OnSelectListener

private const val ARG_OPEN_AT = "open_at"
private const val ARG_MIN_DATE = "min_date"
private const val ARG_MAX_DATE = "max_date"
private const val ARG_PRIMARY_COLOR = "primary_color"

class CalenderPickerFragment(private val onSelectListener: OnSelectListener) : DialogFragment() {

    private val openAt: Long? by lazy { arguments?.getLong(ARG_OPEN_AT) }
    private val minDate: Long? by lazy { arguments?.getLong(ARG_MIN_DATE) }
    private val maxDate: Long? by lazy { arguments?.getLong(ARG_MAX_DATE) }
    val primaryColor: Int? by lazy { arguments?.getInt(ARG_PRIMARY_COLOR) }

    private lateinit var pickerFragmentBinding: FragmentCalenderPickerBinding

    override fun getTheme() = R.style.DialogTheme

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        pickerFragmentBinding = FragmentCalenderPickerBinding.inflate(inflater, container, false)
        pickerFragmentBinding.fragment = this
        return pickerFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pickerFragmentBinding.etCalendarView.let {
            it.openAt = openAt
            it.minDate = minDate
            it.maxDate = maxDate
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(
            openAt: Long? = null, minDate: Long? = null, maxDate: Long? = null,
            @ColorRes primaryColor: Int? = null, onSelectListener: OnSelectListener
        ) =
            CalenderPickerFragment(onSelectListener).apply {
                arguments = bundleOf(
                    ARG_OPEN_AT to openAt, ARG_MIN_DATE to minDate, ARG_MAX_DATE to maxDate,
                    ARG_PRIMARY_COLOR to primaryColor
                )
            }
    }
}