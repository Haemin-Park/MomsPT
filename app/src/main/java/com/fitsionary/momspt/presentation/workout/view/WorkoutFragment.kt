package com.fitsionary.momspt.presentation.workout.view

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.fitsionary.momspt.BR
import com.fitsionary.momspt.R
import com.fitsionary.momspt.data.model.WorkoutModel
import com.fitsionary.momspt.databinding.FragmentWorkoutBinding
import com.fitsionary.momspt.databinding.ItemTypeBinding
import com.fitsionary.momspt.databinding.ItemWorkoutLargeBinding
import com.fitsionary.momspt.presentation.base.BaseFragment
import com.fitsionary.momspt.presentation.base.BaseRecyclerViewAdapter
import com.fitsionary.momspt.presentation.base.BaseViewHolder
import com.fitsionary.momspt.presentation.custom.CustomStepPickerDialog
import com.fitsionary.momspt.presentation.workout.viewmodel.WorkoutViewModel
import com.fitsionary.momspt.util.ext.bindItems
import com.fitsionary.momspt.util.listener.OnItemClickListener

class WorkoutFragment :
    BaseFragment<FragmentWorkoutBinding, WorkoutViewModel>(R.layout.fragment_workout) {
    override val viewModel: WorkoutViewModel by lazy {
        ViewModelProvider(this).get(WorkoutViewModel::class.java)
    }
    private val workoutAdapter =
        object : BaseRecyclerViewAdapter<ItemWorkoutLargeBinding, WorkoutModel>(
            layoutResId = R.layout.item_workout_large,
            bindingVariableItemId = BR.LargeWorkoutItem,
            bindingVariableListenerId = BR.LargeWorkoutItemListener,
        ) {
            override fun onBindViewHolder(
                holder: BaseViewHolder<ItemWorkoutLargeBinding, WorkoutModel>,
                position: Int
            ) {
                super.onBindViewHolder(holder, position)
                val workoutTypeAdapter =
                    object : BaseRecyclerViewAdapter<ItemTypeBinding, String>(
                        layoutResId = R.layout.item_type,
                        bindingVariableItemId = BR.TypeItem
                    ) {}
                holder.binding.rvWorkoutLargeType.adapter = workoutTypeAdapter
                holder.binding.rvWorkoutLargeType.bindItems(currentList[position].type)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finish()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.run {
            vm = viewModel
            rvWorkout.adapter = workoutAdapter
        }

        viewModel.run {
            getTodayInfo()
            getTodayWorkoutList()
        }

        binding.layoutStep.setOnClickListener {
            val dialog = CustomStepPickerDialog.CustomStepPickerDialogBuilder()
                .setOnOkClickedListener {
                }
                .create()
            dialog.show(parentFragmentManager, dialog.tag)
        }

        workoutAdapter.onItemClickListener = object : OnItemClickListener<WorkoutModel> {
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