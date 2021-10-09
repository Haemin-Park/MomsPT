package com.fitsionary.momspt.presentation.workoutdetail.view

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.fitsionary.momspt.BR
import com.fitsionary.momspt.R
import com.fitsionary.momspt.data.model.WorkoutModel
import com.fitsionary.momspt.databinding.FragmentWorkoutDetailBinding
import com.fitsionary.momspt.databinding.ItemTypeBinding
import com.fitsionary.momspt.presentation.base.BaseFragment
import com.fitsionary.momspt.presentation.base.BaseRecyclerViewAdapter
import com.fitsionary.momspt.presentation.workoutdetail.viewmodel.WorkoutDetailViewModel
import timber.log.Timber

class WorkoutDetailFragment
    :
    BaseFragment<FragmentWorkoutDetailBinding, WorkoutDetailViewModel>(R.layout.fragment_workout_detail) {
    val safeArgs: WorkoutDetailFragmentArgs by navArgs()
    private lateinit var workoutItem: WorkoutModel
    override val viewModel: WorkoutDetailViewModel by lazy {
        ViewModelProvider(
            this,
            WorkoutDetailViewModel.ViewModelFactory(
                requireActivity().application,
                workoutItem.workoutCode
            )
        ).get(WorkoutDetailViewModel::class.java)
    }

    private val workoutTypeAdapter =
        object : BaseRecyclerViewAdapter<ItemTypeBinding, String>(
            layoutResId = R.layout.item_type,
            bindingVariableItemId = BR.TypeItem
        ) {}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        workoutItem = safeArgs.workout
        binding.workoutItem = workoutItem

        binding.rvWorkoutDetailType.adapter = workoutTypeAdapter
        viewModel.isAlreadyExistWorkout.observe(viewLifecycleOwner, {
            Timber.i("야호 $it")
        }
        )
        binding.btnPlayWorkout.setOnClickListener {
            viewModel.isAlreadyExistWorkout.value?.let { isExist ->
                if (isExist)
                    startWorkoutPlay()
                else
                    viewModel.downloadLandmarks()
            }
        }

        viewModel.loadingStatus.observe(viewLifecycleOwner, { isLoading ->
            when (isLoading) {
                true -> showLoading()
                false -> hideLoading()
            }
        })

        viewModel.ableNavigation.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let { isAble ->
                if (isAble)
                    startWorkoutPlay()
            }
        })
    }

    private fun startWorkoutPlay() {
        findNavController().navigate(
            WorkoutDetailFragmentDirections.actionWorkoutDetailFragmentToWorkoutPlayMainActivity(
                workoutItem
            )
        )
    }
}