package com.mkb.ethiopian.sample

import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.mkb.ethiopian.lib.CalenderPickerFragment
import com.mkb.ethiopian.lib.models.OnSelectListener
import com.mkb.ethiopian.sample.databinding.FragmentFirstBinding
import java.util.Calendar

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.calendarView.buildCalendar(
            openAt = Calendar.getInstance().let {
                it.set(2006, 9, 2); it.timeInMillis
            },
            minDate = Calendar.getInstance().let {
                it.set(2004, 8, 2); it.timeInMillis
            },
            maxDate = Calendar.getInstance().let {
                it.set(2006, 9, 13); it.timeInMillis
            },
            primaryColor = ContextCompat.getColor(requireContext(), com.mkb.ethiopian.lib.R.color.colorPrimary),
            onSelectListener = object : OnSelectListener {
                override fun onDateSelect(date: Long) {
                    val dateString = DateFormat.format("dd-MM-yyyy", date)
                    Toast.makeText(
                        requireContext(),
                        "Selected Date: $dateString",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )

        binding.btnPicker.setOnClickListener { openDatePicker() }
    }

    private fun openDatePicker() {
        val calendarPickerFragment = CalenderPickerFragment.newInstance(
            openAt = Calendar.getInstance().let {
                it.set(2006, 9, 2); it.timeInMillis
            },
            minDate = Calendar.getInstance().let {
                it.set(2006, 5, 2); it.timeInMillis
            },
            maxDate = Calendar.getInstance().let {
                it.set(2006, 9, 20); it.timeInMillis
            },
            primaryColor = ContextCompat.getColor(requireContext(), R.color.ImperialRed),
            onSelectListener = object : OnSelectListener {
                override fun onDateSelect(date: Long) {
                    val dateString = DateFormat.format("dd-MM-yyyy", date)
                    Toast.makeText(
                        requireContext(),
                        "Selected Date: $dateString",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
        calendarPickerFragment.show(childFragmentManager, "Date Picker")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}