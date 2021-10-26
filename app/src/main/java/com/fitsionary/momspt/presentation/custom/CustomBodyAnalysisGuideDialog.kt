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
import com.fitsionary.momspt.databinding.CustomDialogBodyAnalysisGuideBinding
import com.fitsionary.momspt.presentation.base.BaseDialogFragment

class CustomBodyAnalysisGuideDialog :
    BaseDialogFragment<CustomDialogBodyAnalysisGuideBinding>(R.layout.custom_dialog_body_analysis_guide) {

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

        binding.btnGuideCancel.setOnClickListener {
            dismiss()
        }
    }
}