package com.fitsionary.momspt.presentation.workoutplay.view

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.fitsionary.momspt.R
import com.fitsionary.momspt.databinding.FragmentWorkoutResultBinding
import com.fitsionary.momspt.presentation.base.BaseFragment
import com.fitsionary.momspt.presentation.workoutplay.viewmodel.WorkoutResultViewModel

class WorkoutResultFragment
    :
    BaseFragment<FragmentWorkoutResultBinding, WorkoutResultViewModel>(R.layout.fragment_workout_result) {
    override val viewModel: WorkoutResultViewModel by lazy {
        ViewModelProvider(this).get(WorkoutResultViewModel::class.java)
    }
    val safeArgs: WorkoutResultFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val resultScore = safeArgs.resultScore
        binding.tvResultCumulativeScore.text = "${resultScore}점"
        binding.btnClose.setOnClickListener {
            requireActivity().finish()
        }
    }
}