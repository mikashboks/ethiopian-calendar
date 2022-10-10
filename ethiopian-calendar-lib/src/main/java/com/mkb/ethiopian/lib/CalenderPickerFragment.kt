package com.mkb.ethiopian.lib

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.ColorRes
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.mkb.ethiopian.lib.databinding.FragmentCalenderPickerBinding
import com.mkb.ethiopian.lib.models.OnSelectListener

private const val ARG_OPEN_AT = "open_at"
private const val ARG_MIN_DATE = "min_date"
private const val ARG_MAX_DATE = "max_date"
private const val ARG_PRIMARY_COLOR = "primary_color"

class CalenderPickerFragment() : DialogFragment() {

    private var onSelectListener: OnSelectListener? = null

    private constructor(onSelectListener: OnSelectListener) : this() {
        this.onSelectListener = onSelectListener
    }

    private val openAt: Long? by lazy { arguments?.getLong(ARG_OPEN_AT, 0) }
    private val minDate: Long? by lazy { arguments?.getLong(ARG_MIN_DATE, 0) }
    private val maxDate: Long? by lazy { arguments?.getLong(ARG_MAX_DATE, 0) }
    val primaryColor: Int? by lazy { arguments?.getInt(ARG_PRIMARY_COLOR, 0) }

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
            onSelectListener?.let { onSelect -> it.onSelectListener = onSelect }
        }
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    companion object {

        @JvmStatic
        fun newInstance(
            openAt: Long? = null, minDate: Long? = null, maxDate: Long? = null,
            @ColorRes primaryColor: Int? = null, onSelectListener: OnSelectListener
        ) = CalenderPickerFragment(onSelectListener).apply {
            arguments = bundleOf(
                ARG_OPEN_AT to openAt, ARG_MIN_DATE to minDate, ARG_MAX_DATE to maxDate,
                ARG_PRIMARY_COLOR to primaryColor
            )
        }
    }
}