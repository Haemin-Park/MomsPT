package com.fitsionary.momspt.presentation.workout.view

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.fitsionary.momspt.R
import com.fitsionary.momspt.databinding.DialogFragmentPlayerControlBinding
import com.fitsionary.momspt.presentation.base.BaseDialogFragment
import com.fitsionary.momspt.presentation.workout.viewmodel.PlayerControlDialogViewModel

class PlayerControlDialogFragment(isPlaying: Boolean) :
    BaseDialogFragment<DialogFragmentPlayerControlBinding, PlayerControlDialogViewModel>(R.layout.dialog_fragment_player_control) {
    override val viewModel: PlayerControlDialogViewModel by lazy {
        ViewModelProvider(this, PlayerControlDialogViewModel.ViewModelFactory(isPlaying)).get(
            PlayerControlDialogViewModel::class.java
        )
    }

    private lateinit var listener: MyDialogClickedListener

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel

        dialog?.apply {
            setCanceledOnTouchOutside(true)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        binding.btnControl.setOnClickListener {
            listener.onControllerClicked()
        }
    }

    fun setState(isPlaying: Boolean) {
        viewModel.setStatus(isPlaying)
    }

    fun setOnClickedListener(listener: () -> Unit) {
        this.listener = object : MyDialogClickedListener {
            override fun onControllerClicked() {
                listener()
            }
        }
    }

    interface MyDialogClickedListener {
        fun onControllerClicked()
    }

    companion object {
        fun newInstance(isPlaying: Boolean) = PlayerControlDialogFragment(isPlaying)
        const val PLAYER_CONTROL_DIALOG_FRAGMENT_TAG = "PLAYER_CONTROL_DIALOG_FRAGMENT"
    }
}