package com.fitsionary.momspt.presentation.workout.view

import androidx.lifecycle.ViewModelProvider
import com.fitsionary.momspt.R
import com.fitsionary.momspt.databinding.FragmentWorkoutBinding
import com.fitsionary.momspt.presentation.base.BaseFragment
import com.fitsionary.momspt.presentation.workout.viewmodel.WorkoutViewModel

class WorkoutFragment :
    BaseFragment<FragmentWorkoutBinding, WorkoutViewModel>(R.layout.fragment_workout) {
    override val viewModel: WorkoutViewModel by lazy {
        ViewModelProvider(this).get(WorkoutViewModel::class.java)
    }
}