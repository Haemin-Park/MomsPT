package com.fitsionary.momspt.presentation.workoutplay.view

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.navArgs
import androidx.navigation.ui.NavigationUI
import com.fitsionary.momspt.R
import com.fitsionary.momspt.databinding.ActivityWorkoutPlayMainBinding
import com.fitsionary.momspt.presentation.base.BaseActivity
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
        val safeArgs: WorkoutPlayMainActivityArgs by navArgs()
        val bundle = Bundle()
        bundle.putParcelable("workout", safeArgs.workout)
        val navController = this.findNavController(R.id.workout_play_nav_host_fragment)
        navController.setGraph(R.navigation.workout_play_nav_graph, bundle)
    }
}