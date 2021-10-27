package com.fitsionary.momspt.presentation.workoutplay.view

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.fitsionary.momspt.R
import com.fitsionary.momspt.data.enum.RankEnum.Companion.makeScoreToRank
import com.fitsionary.momspt.data.model.WorkoutModel
import com.fitsionary.momspt.databinding.FragmentWorkoutResultBinding
import com.fitsionary.momspt.presentation.base.BaseFragment
import com.fitsionary.momspt.presentation.binding.setCircleImageFromImageUrl
import com.fitsionary.momspt.presentation.binding.setRankText
import com.fitsionary.momspt.presentation.workoutdetail.view.WORKOUT
import com.fitsionary.momspt.presentation.workoutplay.viewmodel.WorkoutResultViewModel
import com.fitsionary.momspt.util.DateUtil.getRequestDateFormat

class WorkoutResultFragment
    :
    BaseFragment<FragmentWorkoutResultBinding, WorkoutResultViewModel>(R.layout.fragment_workout_result) {
    override val viewModel: WorkoutResultViewModel by lazy {
        ViewModelProvider(
            this,
            WorkoutResultViewModel.ViewModelFactory(requireActivity().application)
        ).get(WorkoutResultViewModel::class.java)
    }
    val safeArgs: WorkoutResultFragmentArgs by navArgs()
    private var nextWorkout: WorkoutModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val workoutItem = safeArgs.workout

        binding.workoutItem = workoutItem
        binding.vm = viewModel

        val rank = makeScoreToRank(safeArgs.resultScore, workoutItem.workoutAnalysisType)
        viewModel.sendWorkoutResult(workoutItem.workoutId, getRequestDateFormat(), rank)
        setRankText(binding.tvResultCumulativeScore, rank)

        viewModel.nextWorkout.observe(viewLifecycleOwner, {
            if (it != null) {
                nextWorkout = it
                setCircleImageFromImageUrl(binding.ivNext, it.thumbnail)
                binding.layoutNextWorkout.visibility = View.VISIBLE
            }
        })

        viewModel.isAlreadyExistWorkout.observe(viewLifecycleOwner, {})
        binding.layoutNextWorkout.setOnClickListener {
            viewModel.isAlreadyExistWorkout.value?.let { isExist ->
                if (isExist)
                    findNavController().navigate(
                        WorkoutResultFragmentDirections.actionWorkoutResultFragmentToWorkoutPlayFragment(
                            nextWorkout!!
                        )
                    )
                else {
                    requireActivity().setResult(
                        RESULT_OK,
                        Intent(
                            requireContext(),
                            WorkoutPlayMainActivity::class.java
                        ).putExtra(WORKOUT, nextWorkout)
                    )
                    requireActivity().finish()
                }
            }
        }
        binding.btnClose.setOnClickListener {
            requireActivity().finish()
        }
    }
}