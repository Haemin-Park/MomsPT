package com.fitsionary.momspt.presentation.workoutdetail.view

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.fitsionary.momspt.R
import com.fitsionary.momspt.databinding.FragmentWorkoutDetailBinding
import com.fitsionary.momspt.presentation.base.BaseFragment
import com.fitsionary.momspt.presentation.workoutdetail.viewmodel.WorkoutDetailViewModel

class WorkoutDetailFragment
    :
    BaseFragment<FragmentWorkoutDetailBinding, WorkoutDetailViewModel>(R.layout.fragment_workout_detail) {
    override val viewModel: WorkoutDetailViewModel by lazy {
        ViewModelProvider(this).get(WorkoutDetailViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val safeArgs: WorkoutDetailFragmentArgs by navArgs()
        val workoutItem = safeArgs.workout
        binding.workoutItem = workoutItem

        binding.btnPlayWorkout.setOnClickListener {
            viewModel.downloadLandmarks()
        }

        viewModel.loadingStatus.observe(this, { isLoading ->
            when (isLoading) {
                true -> showLoading()
                false -> hideLoading()
            }
        })

        viewModel.ableNavigation.observe(this, {
            it.getContentIfNotHandled()?.let { isAble ->
                if (isAble)
                    findNavController().navigate(
                        WorkoutDetailFragmentDirections.actionWorkoutDetailFragmentToWorkoutPlayActivity(
                            workoutItem.name
                        )
                    )
            }
        })
    }
}