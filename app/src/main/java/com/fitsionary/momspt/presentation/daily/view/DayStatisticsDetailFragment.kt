package com.fitsionary.momspt.presentation.daily.view

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.fitsionary.momspt.BR
import com.fitsionary.momspt.R
import com.fitsionary.momspt.data.model.WorkoutModel
import com.fitsionary.momspt.databinding.FragmentDayStatisticsDetailBinding
import com.fitsionary.momspt.databinding.FragmentWorkoutBinding
import com.fitsionary.momspt.presentation.base.BaseFragment
import com.fitsionary.momspt.presentation.base.BaseRecyclerViewAdapter
import com.fitsionary.momspt.presentation.daily.viewmodel.DayStatisticsDetailViewModel

class DayStatisticsDetailFragment :
    BaseFragment<FragmentDayStatisticsDetailBinding, DayStatisticsDetailViewModel>(R.layout.fragment_day_statistics_detail) {
    override val viewModel: DayStatisticsDetailViewModel by lazy {
        ViewModelProvider(this).get(DayStatisticsDetailViewModel::class.java)
    }

    private val workoutAdapter =
        object : BaseRecyclerViewAdapter<FragmentWorkoutBinding, WorkoutModel>(
            layoutResId = R.layout.item_workout_large,
            bindingVariableItemId = BR.LargeWorkoutItem
        ) {}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.run {
            vm = viewModel
            rvDayDetailWorkout.adapter = workoutAdapter
        }
    }
}