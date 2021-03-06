package com.fitsionary.momspt.presentation.custom

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Display
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.navigation.fragment.navArgs
import com.fitsionary.momspt.R
import com.fitsionary.momspt.databinding.CustomDialogStepPickerBinding
import com.fitsionary.momspt.presentation.base.BaseDialogFragment
import com.fitsionary.momspt.util.NavResult
import com.fitsionary.momspt.util.setNavResult

class CustomStepPickerDialog :
    BaseDialogFragment<CustomDialogStepPickerBinding>(R.layout.custom_dialog_step_picker) {
    val safeArgs: CustomStepPickerDialogArgs by navArgs()

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
        safeArgs.run {
            settingPicker(step)
            binding.pickerStep.value = step
            binding.pickerStepDay.value = stepDay
        }

        binding.btnStepPickerCancel.setOnClickListener {
            dismiss()
        }
        binding.btnStepPickerChoice.setOnClickListener {
            setNavResult(
                NavResult.ChoiceResult(
                    binding.pickerStep.value,
                    binding.pickerStepDay.value
                )
            )
            dismiss()
        }
        binding.pickerStep.setOnValueChangedListener { _, _, newVal ->
            settingPicker(newVal)
        }
    }

    private fun settingPicker(step: Int) {
        binding.pickerStepDay.maxValue = when (step) {
            1 -> 7
            2 -> 23
            3 -> 20
            4 -> 50
            else -> 80
        }
    }
}