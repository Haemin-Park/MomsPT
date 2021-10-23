package com.fitsionary.momspt.presentation.loading.view

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.fitsionary.momspt.R
import com.fitsionary.momspt.databinding.DialogFragmentLoadingBinding
import com.fitsionary.momspt.presentation.base.BaseDialogFragment

class LoadingDialogFragment
    : BaseDialogFragment<DialogFragmentLoadingBinding>(R.layout.dialog_fragment_loading) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.apply {
            setCancelable(false)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        Glide.with(this).asGif().load(R.raw.loading_indicator).into(binding.ivLoading)
    }

    companion object {
        fun newInstance() = LoadingDialogFragment()
        const val LOADING_DIALOG_FRAGMENT_TAG = "LOADING_DIALOG_FRAGMENT"
    }
}