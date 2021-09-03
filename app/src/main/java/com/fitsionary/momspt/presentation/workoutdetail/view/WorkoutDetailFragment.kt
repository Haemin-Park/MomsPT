package com.fitsionary.momspt.presentation.workoutdetail.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.fitsionary.momspt.R
import com.fitsionary.momspt.databinding.FragmentWorkoutDetailBinding
import com.fitsionary.momspt.presentation.base.BaseFragment
import com.fitsionary.momspt.presentation.home.view.HomeFragment.Companion.WORKOUT_NAME
import com.fitsionary.momspt.presentation.main.view.MainActivity
import com.fitsionary.momspt.presentation.workout.view.WorkoutPlayActivity
import com.fitsionary.momspt.presentation.workoutdetail.viewmodel.WorkoutDetailViewModel
import kotlinx.android.synthetic.main.activity_main.*

class WorkoutDetailFragment
    :
    BaseFragment<FragmentWorkoutDetailBinding, WorkoutDetailViewModel>(R.layout.fragment_workout_detail) {
    override val viewModel: WorkoutDetailViewModel by lazy {
        ViewModelProvider(this).get(WorkoutDetailViewModel::class.java)
    }
    private lateinit var currentActivity: MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity)
            currentActivity = activity as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sageArgs: WorkoutDetailFragmentArgs by navArgs()
        val workoutItem = sageArgs.workout
        binding.workoutItem = workoutItem

        binding.btnPlayWorkout.setOnClickListener {
            startActivity(
                Intent(currentActivity, WorkoutPlayActivity::class.java).putExtra(
                    WORKOUT_NAME, workoutItem.name
                )
            )
        }
    }
}