package com.fitsionary.momspt.presentation.workoutplay.view

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.fitsionary.momspt.R
import com.fitsionary.momspt.data.model.WorkoutModel
import com.fitsionary.momspt.databinding.ActivityWorkoutPlayMainBinding
import com.fitsionary.momspt.presentation.base.BaseActivity
import com.fitsionary.momspt.presentation.workoutdetail.view.WORKOUT
import com.fitsionary.momspt.presentation.workoutplay.viewmodel.WorkoutPlayMainViewModel

class WorkoutPlayMainActivity :
    BaseActivity<ActivityWorkoutPlayMainBinding, WorkoutPlayMainViewModel>(
        R.layout.activity_workout_play_main
    ) {
    override val viewModel: WorkoutPlayMainViewModel by lazy {
        ViewModelProvider(this).get(WorkoutPlayMainViewModel::class.java)
    }

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val workout: WorkoutModel = intent.getParcelableExtra<WorkoutModel>(WORKOUT) as WorkoutModel
        val bundle = Bundle()
        bundle.putParcelable(WORKOUT, workout)
        val navController = this.findNavController(R.id.workout_play_nav_host_fragment)
        navController.setGraph(R.navigation.workout_play_nav_graph, bundle)
    }
}