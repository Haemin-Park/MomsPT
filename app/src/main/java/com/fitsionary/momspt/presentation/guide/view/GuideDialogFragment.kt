package com.fitsionary.momspt.presentation.guide.view

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import com.fitsionary.momspt.R
import com.fitsionary.momspt.databinding.DialogFragmentGuideBinding
import com.fitsionary.momspt.presentation.base.BaseDialogFragment

class GuideDialogFragment :
    BaseDialogFragment<DialogFragmentGuideBinding>(R.layout.dialog_fragment_guide) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.apply {
            setCancelable(false)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    companion object {
        fun newInstance() = GuideDialogFragment()
        const val GUIDE_DIALOG_FRAGMENT_TAG = "GUIDE_DIALOG_FRAGMENT"
    }
}