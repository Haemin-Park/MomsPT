package com.fitsionary.momspt.presentation.daily.view

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.fitsionary.momspt.BR
import com.fitsionary.momspt.R
import com.fitsionary.momspt.data.model.WorkoutModel
import com.fitsionary.momspt.databinding.FragmentDayStatisticsDetailBinding
import com.fitsionary.momspt.databinding.FragmentWorkoutBinding
import com.fitsionary.momspt.presentation.base.BaseFragment
import com.fitsionary.momspt.presentation.base.BaseRecyclerViewAdapter
import com.fitsionary.momspt.presentation.daily.viewmodel.DayStatisticsDetailViewModel
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

class DayStatisticsDetailFragment :
    BaseFragment<FragmentDayStatisticsDetailBinding, DayStatisticsDetailViewModel>(R.layout.fragment_day_statistics_detail) {
    override val viewModel: DayStatisticsDetailViewModel by lazy {
        ViewModelProvider(this).get(DayStatisticsDetailViewModel::class.java)
    }
    val safeArgs: DayStatisticsDetailFragmentArgs by navArgs()

    private val workoutAdapter =
        object : BaseRecyclerViewAdapter<FragmentWorkoutBinding, WorkoutModel>(
            layoutResId = R.layout.item_workout_large,
            bindingVariableItemId = BR.LargeWorkoutItem
        ) {}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val date = safeArgs.date
        binding.run {
            vm = viewModel
            rvDayDetailWorkout.adapter = workoutAdapter
        }
        val formattedDate = date.format(DateTimeFormatter.ofPattern("MM월 dd일"))
        val dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN)
        binding.tvDayDetailDate.text = getString(
            R.string.day_detail_date_format,
            formattedDate,
            dayOfWeek
        )

        viewModel.getDayStatisticsDetail(date.toString())
        viewModel.dayStatistics.observe(viewLifecycleOwner, {
            binding.layoutDayDetailTime.setBodyText(it.time.toString())
            binding.layoutDayDetailKcal.setBodyText(it.kcal.toString())
        })

        binding.btnPreDate.setOnClickListener {
            findNavController().navigate(
                DayStatisticsDetailFragmentDirections.actionDayStatisticsDetailFragmentSelf(
                    date.minusDays(1)
                )
            )
        }
        binding.btnNextDate.setOnClickListener {
            findNavController().navigate(
                DayStatisticsDetailFragmentDirections.actionDayStatisticsDetailFragmentSelf(
                    date.plusDays(1)
                )
            )
        }
    }
}