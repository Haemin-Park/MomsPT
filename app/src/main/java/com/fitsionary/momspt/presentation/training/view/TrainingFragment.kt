package com.fitsionary.momspt.presentation.training.view

import androidx.lifecycle.ViewModelProvider
import com.fitsionary.momspt.R
import com.fitsionary.momspt.databinding.FragmentTrainingBinding
import com.fitsionary.momspt.presentation.base.BaseFragment
import com.fitsionary.momspt.presentation.training.viewmodel.TrainingViewModel

class TrainingFragment :
    BaseFragment<FragmentTrainingBinding, TrainingViewModel>(R.layout.fragment_training) {
    override val viewModel: TrainingViewModel by lazy {
        ViewModelProvider(this).get(TrainingViewModel::class.java)
    }
}