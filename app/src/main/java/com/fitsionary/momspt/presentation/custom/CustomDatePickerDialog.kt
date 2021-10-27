package com.fitsionary.momspt.presentation.custom

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Display
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.fitsionary.momspt.R
import com.fitsionary.momspt.databinding.CustomDialogDatePickerBinding
import com.fitsionary.momspt.presentation.base.BaseDialogFragment
import com.fitsionary.momspt.util.NavResult
import com.fitsionary.momspt.util.setNavResult
import java.util.*

class CustomDatePickerDialog :
    BaseDialogFragment<CustomDialogDatePickerBinding>(R.layout.custom_dialog_date_picker) {
    private val calendar: Calendar = Calendar.getInstance()
    override fun onResume() {
        super.onResume()

        val outMetrics = DisplayMetrics()
        val display: Display?

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            display = activity?.display
            display?.getRealMetrics(outMetrics)
        } else {
            @Suppress("DEPRECATION")
            display = activity?.windowManager?.defaultDisplay
            @Suppress("DEPRECATION")
            display?.getMetrics(outMetrics)
        }

        val params: ViewGroup.LayoutParams? = dialog?.window?.attributes
        val deviceWidth = outMetrics.widthPixels

        params?.width = (deviceWidth * 0.9).toInt()
        dialog?.window?.attributes = params as WindowManager.LayoutParams
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.apply {
            binding.btnCancel.setOnClickListener {
                dismiss()
            }
            binding.btnChoice.setOnClickListener {
                setNavResult(
                    NavResult.Date(
                        binding.pickerYear.value,
                        binding.pickerMonth.value,
                        binding.pickerDay.value
                    )
                )
                dismiss()
            }
        }
        initYearPicker()
        initMonthPicker()
        initDayPicker()
        binding.pickerMonth.setOnValueChangedListener { _, _, _ ->
            settingDayPicker()
        }
    }

    private fun initYearPicker() {
        val year: Int = calendar.get(Calendar.YEAR)
        binding.pickerYear.minValue = year - 1
        binding.pickerYear.maxValue = year + 1
        binding.pickerYear.value = year
    }

    private fun initMonthPicker() {
        val month: Int = calendar.get(Calendar.MONTH)
        binding.pickerMonth.value = month + 1
    }

    private fun initDayPicker() {
        val day: Int = calendar.get(Calendar.DATE)
        binding.pickerDay.value = day
        settingDayPicker()
    }

    private fun settingDayPicker() {
        calendar.set(binding.pickerYear.value, binding.pickerMonth.value - 1, 1)
        val maximumDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        binding.pickerDay.maxValue = maximumDay
    }
}