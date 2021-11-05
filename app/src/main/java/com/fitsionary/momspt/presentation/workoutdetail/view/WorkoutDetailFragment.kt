package com.fitsionary.momspt.presentation.workoutdetail.view

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
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
import com.fitsionary.momspt.presentation.workoutplay.view.WorkoutPlayMainActivity


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
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private val workoutTypeAdapter =
        object : BaseRecyclerViewAdapter<ItemTypeBinding, String>(
            layoutResId = R.layout.item_type,
            bindingVariableItemId = BR.TypeItem
        ) {}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        workoutItem = safeArgs.workout
        binding.workoutItem = workoutItem
        resultLauncher = registerForActivityResult(StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                val nextWorkout = data?.getParcelableExtra<WorkoutModel>(WORKOUT)
                nextWorkout?.let {
                    findNavController().navigate(
                        WorkoutDetailFragmentDirections.actionWorkoutDetailFragmentSelf(
                            it
                        )
                    )
                }
            }
        }

        binding.rvWorkoutDetailType.adapter = workoutTypeAdapter
        viewModel.isAlreadyExistWorkout.observe(viewLifecycleOwner, {})
        binding.btnPlayWorkout.setOnClickListener {
            viewModel.isAlreadyExistWorkout.value?.let { isExist ->
                if (isExist || !workoutItem.ai)
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
        val intent = Intent(
            requireContext(),
            WorkoutPlayMainActivity::class.java
        ).putExtra("workout", workoutItem)
        resultLauncher.launch(intent)
    }
}

const val WORKOUT = "workout"