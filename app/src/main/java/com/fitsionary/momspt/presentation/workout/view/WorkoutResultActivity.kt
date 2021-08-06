package com.fitsionary.momspt.presentation.workout.view

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.fitsionary.momspt.R
import com.fitsionary.momspt.databinding.ActivityWorkoutResultBinding
import com.fitsionary.momspt.presentation.base.BaseActivity
import com.fitsionary.momspt.presentation.workout.viewmodel.WorkoutResultViewModel

class WorkoutResultActivity
    :
    BaseActivity<ActivityWorkoutResultBinding, WorkoutResultViewModel>(R.layout.activity_workout_result) {
    override val viewModel: WorkoutResultViewModel by lazy {
        ViewModelProvider(this).get(WorkoutResultViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val resultCumulativeScore = intent.getIntExtra(RESULT_CUMULATIVE_SCORE, 0)
        binding.tvResultCumulativeScore.text = "${resultCumulativeScore}점"

        binding.btnClose.setOnClickListener {
            finish()
        }
    }

    companion object {
        const val RESULT_CUMULATIVE_SCORE = "RESULT_CUMULATIVE_SCORE"
    }
}