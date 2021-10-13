package com.fitsionary.momspt.presentation.custom

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Display
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.fitsionary.momspt.R
import com.fitsionary.momspt.databinding.CustomDialogEditTodayWeightBinding
import com.fitsionary.momspt.presentation.base.BaseDialogFragment
import com.fitsionary.momspt.util.NAV_RESULT_KEY
import com.fitsionary.momspt.util.NavResult

class CustomEditTodayWeightDialog :
    BaseDialogFragment<CustomDialogEditTodayWeightBinding>(R.layout.custom_dialog_edit_today_weight) {

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

    @SuppressLint("ShowToast")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.apply {
            binding.btnCancel.setOnClickListener {
                setNavResult(NavResult.Cancel)
                dismiss()
            }
            binding.btnConfirm.setOnClickListener {
                val text: String = binding.etEditWeight.text.toString()
                if (text.isNotEmpty()) try {
                    setNavResult(NavResult.TodayWeight(text.toDouble()))
                    dismiss()
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "숫자와 소수점만 입력 가능합니다.", Toast.LENGTH_SHORT)
                        .show()
                    e.printStackTrace()
                }
            }
        }
    }

    private fun setNavResult(answer: NavResult) {
        findNavController().previousBackStackEntry?.apply {
            savedStateHandle.set<NavResult>(
                NAV_RESULT_KEY,
                answer
            )
        }
    }
}