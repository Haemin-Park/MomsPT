package com.fitsionary.momspt.presentation.workout.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fitsionary.momspt.R
import com.fitsionary.momspt.presentation.base.BaseViewModel

class PlayerControlDialogViewModel(isPlaying: Boolean) : BaseViewModel() {
    private val _imgRes = MutableLiveData<Int>()

    val imgRes: LiveData<Int>
        get() = _imgRes

    init {
        setImage(isPlaying)
    }

    class ViewModelFactory(private val isPlaying: Boolean) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(PlayerControlDialogViewModel::class.java)) {
                PlayerControlDialogViewModel(isPlaying) as T
            } else {
                throw IllegalArgumentException()
            }
        }
    }

    fun setStatus(isPlaying: Boolean) {
        setImage(isPlaying)
    }

    private fun setImage(isPlaying: Boolean) {
        if (isPlaying) {
            _imgRes.value = R.drawable.pause
        } else
            _imgRes.value = R.drawable.ic_play
    }
}