package com.fitsionary.momspt.presentation.guide.view

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.fitsionary.momspt.R
import com.fitsionary.momspt.databinding.DialogFragmentGuideBinding
import com.fitsionary.momspt.presentation.base.BaseDialogFragment
import kotlinx.android.synthetic.main.dialog_fragment_guide.*

class GuideDialogFragment :
    BaseDialogFragment<DialogFragmentGuideBinding>(R.layout.dialog_fragment_guide) {
    private lateinit var guideText: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.apply {
            setCancelable(false)
            window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            if (::guideText.isInitialized)
                binding.tvGuide.text = guideText
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    class GuideDialogBuilder {
        private val dialog = GuideDialogFragment()

        fun setGuideText(guideText: String): GuideDialogBuilder {
            dialog.guideText = guideText
            return this
        }

        fun create() = dialog
    }

    companion object {
        const val WORKOUT_GUIDE_DIALOG_FRAGMENT_TAG = "WORKOUT_GUIDE_DIALOG_FRAGMENT"
        const val AI_GUIDE_DIALOG_FRAGMENT_TAG = "AI_GUIDE_DIALOG_FRAGMENT"
    }
}