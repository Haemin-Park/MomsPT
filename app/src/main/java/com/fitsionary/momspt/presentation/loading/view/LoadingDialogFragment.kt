package com.fitsionary.momspt.presentation.loading.view

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.fitsionary.momspt.R
import com.fitsionary.momspt.databinding.DialogFragmentLoadingBinding
import com.fitsionary.momspt.presentation.base.BaseDialogFragment
import com.fitsionary.momspt.presentation.loading.viewmodel.LoadingViewModel

class LoadingDialogFragment
    : BaseDialogFragment<DialogFragmentLoadingBinding, LoadingViewModel>(R.layout.dialog_fragment_loading) {
    override val viewModel: LoadingViewModel by lazy {
        ViewModelProvider(this).get(LoadingViewModel::class.java)
    }

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