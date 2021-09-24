package com.fitsionary.momspt.presentation.workout.view

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.fitsionary.momspt.BR
import com.fitsionary.momspt.R
import com.fitsionary.momspt.data.api.request.TodayWorkoutListRequest
import com.fitsionary.momspt.data.model.WorkoutModel
import com.fitsionary.momspt.databinding.FragmentWorkoutBinding
import com.fitsionary.momspt.presentation.base.BaseFragment
import com.fitsionary.momspt.presentation.base.BaseRecyclerViewAdapter
import com.fitsionary.momspt.presentation.custom.CustomStepPickerDialog
import com.fitsionary.momspt.presentation.workout.viewmodel.WorkoutViewModel
import com.fitsionary.momspt.util.DateUtil
import com.fitsionary.momspt.util.TEST_USER_NAME
import com.fitsionary.momspt.util.listener.OnItemClickListener

class WorkoutFragment :
    BaseFragment<FragmentWorkoutBinding, WorkoutViewModel>(R.layout.fragment_workout) {
    override val viewModel: WorkoutViewModel by lazy {
        ViewModelProvider(this).get(WorkoutViewModel::class.java)
    }
    private val routineAdapter =
        object : BaseRecyclerViewAdapter<FragmentWorkoutBinding, WorkoutModel>(
            layoutResId = R.layout.item_workout_large,
            bindingVariableItemId = BR.LargeWorkoutItem,
            bindingVariableListenerId = BR.LargeWorkoutItemListener,
        ) {}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.run {
            vm = viewModel
            rvWorkout.adapter = routineAdapter
        }

        binding.layoutStep.setOnClickListener {
            val dialog = CustomStepPickerDialog.CustomStepPickerDialogBuilder()
                .setOnOkClickedListener {
                }
                .create()
            dialog.show(parentFragmentManager, dialog.tag)
        }

        viewModel.getTodayWorkoutList(
            TodayWorkoutListRequest(
                DateUtil.getRequestDateFormat(),
                TEST_USER_NAME
            )
        )

        routineAdapter.onItemClickListener = object : OnItemClickListener<WorkoutModel> {
            override fun onClick(item: WorkoutModel) {
                findNavController().navigate(
                    WorkoutFragmentDirections.actionMainWorkoutToWorkoutDetailFragment(
                        item
                    )
                )
            }
        }
    }
}