package com.fitsionary.momspt.presentation.home.view

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.fitsionary.momspt.BR
import com.fitsionary.momspt.R
import com.fitsionary.momspt.data.model.DayAchievedModel
import com.fitsionary.momspt.data.model.WorkoutModel
import com.fitsionary.momspt.databinding.FragmentHomeBinding
import com.fitsionary.momspt.databinding.ItemDayAchievedBinding
import com.fitsionary.momspt.databinding.ItemWorkoutMediumBinding
import com.fitsionary.momspt.presentation.base.BaseFragment
import com.fitsionary.momspt.presentation.base.BaseRecyclerViewAdapter
import com.fitsionary.momspt.presentation.home.viewmodel.HomeViewModel
import com.fitsionary.momspt.util.listener.OnItemClickListener


class HomeFragment :
    BaseFragment<FragmentHomeBinding, HomeViewModel>(R.layout.fragment_home) {
    override val viewModel: HomeViewModel by lazy {
        ViewModelProvider(this).get(HomeViewModel::class.java)
    }
    private val workoutAdapter =
        object : BaseRecyclerViewAdapter<ItemWorkoutMediumBinding, WorkoutModel>(
            layoutResId = R.layout.item_workout_medium,
            bindingVariableItemId = BR.MediumWorkoutItem,
            bindingVariableListenerId = BR.MediumWorkoutItemListener
        ) {}

    private val dayAchievedAdapter =
        object : BaseRecyclerViewAdapter<ItemDayAchievedBinding, DayAchievedModel>(
            layoutResId = R.layout.item_day_achieved,
            bindingVariableItemId = BR.DayAchieved
        ) {}

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
            rvRecommendWorkout.adapter = workoutAdapter
            rvDayAchieved.adapter = dayAchievedAdapter
        }

        viewModel.run {
            getTodayInfo()
            getTodayWorkoutList()
            getWeeklyAchieved()
        }

        viewModel.info.observe(viewLifecycleOwner, {
            binding.seekbarCurrentStep.setDay(it.day)
        })

        workoutAdapter.onItemClickListener = object : OnItemClickListener<WorkoutModel> {
            override fun onClick(item: WorkoutModel) {
                findNavController().navigate(
                    HomeFragmentDirections.actionMainHomeToWorkoutDetailFragment(
                        item
                    )
                )
            }
        }
    }
}