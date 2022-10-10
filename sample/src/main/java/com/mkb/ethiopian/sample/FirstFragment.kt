package com.mkb.ethiopian.sample

import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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

        binding.calendarView.openAt = Calendar.getInstance().let {
            it.set(2023, 6, 2); it.timeInMillis
        }

        binding.calendarView.minDate = Calendar.getInstance().let {
            it.set(2023, 5, 1); it.timeInMillis
        }

        binding.calendarView.maxDate = Calendar.getInstance().let {
            it.set(2023, 7, 1); it.timeInMillis
        }

        binding.calendarView.onSelectListener = object : OnSelectListener {
            override fun onDateSelect(date: Long) {
                val dateString = DateFormat.format("dd-MM-yyyy", date)
                Toast.makeText(
                    requireContext(),
                    "Selected Date: $dateString",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.btnPicker.setOnClickListener { openDatePicker() }
    }

    private fun openDatePicker() {
        val calendarPickerFragment = CalenderPickerFragment.newInstance(
            openAt = Calendar.getInstance().let {
                it.set(2023, 6, 2); it.timeInMillis
            },
            minDate = Calendar.getInstance().let {
                it.set(2023, 5, 2); it.timeInMillis
            },
            maxDate = Calendar.getInstance().let {
                it.set(2023, 7, 2); it.timeInMillis
            },
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