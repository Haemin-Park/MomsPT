package com.fitsionary.momspt.presentation.workout.view

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.fitsionary.momspt.R
import com.fitsionary.momspt.data.model.WorkoutModel
import com.fitsionary.momspt.databinding.ActivityWorkoutDetailBinding
import com.fitsionary.momspt.presentation.base.BaseActivity
import com.fitsionary.momspt.presentation.home.view.HomeFragment.Companion.WORKOUT_NAME
import com.fitsionary.momspt.presentation.workout.viewmodel.WorkoutDetailViewModel

class WorkoutDetailActivity
    :
    BaseActivity<ActivityWorkoutDetailBinding, WorkoutDetailViewModel>(R.layout.activity_workout_detail) {
    override val viewModel: WorkoutDetailViewModel by lazy {
        ViewModelProvider(this).get(WorkoutDetailViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val workoutItem = intent.getParcelableExtra<WorkoutModel>(WORKOUT_NAME)!!
        binding.workoutItem = workoutItem

        binding.btnPlayWorkout.setOnClickListener {
            startActivity(
                Intent(this, WorkoutPlayActivity::class.java).putExtra(
                    WORKOUT_NAME, workoutItem.name
                )
            )
        }
    }
}