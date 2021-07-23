package com.fitsionary.momspt.presentation.workout.view

import androidx.lifecycle.ViewModelProvider
import com.fitsionary.momspt.R
import com.fitsionary.momspt.databinding.ActivityWorkoutStartBinding
import com.fitsionary.momspt.presentation.base.BaseActivity
import com.fitsionary.momspt.presentation.workout.viewmodel.WorkoutStartViewModel

class WorkoutStartActivity :
    BaseActivity<ActivityWorkoutStartBinding, WorkoutStartViewModel>(R.layout.activity_workout_start) {
    override val viewModel: WorkoutStartViewModel by lazy {
        ViewModelProvider(this).get(WorkoutStartViewModel::class.java)
    }
}