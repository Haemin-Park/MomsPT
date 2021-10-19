package com.fitsionary.momspt.presentation.daily.view

import android.os.Bundle
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.fitsionary.momspt.R
import com.fitsionary.momspt.databinding.FragmentMonthStatisticsBinding
import com.fitsionary.momspt.presentation.base.BaseFragment
import com.fitsionary.momspt.presentation.daily.viewmodel.MonthStatisticsViewModel
import com.fitsionary.momspt.util.EventDecorator
import java.time.LocalDate


class MonthStatisticsFragment :
    BaseFragment<FragmentMonthStatisticsBinding, MonthStatisticsViewModel>(
        R.layout.fragment_month_statistics
    ) {
    override val viewModel: MonthStatisticsViewModel by lazy {
        ViewModelProvider(this).get(MonthStatisticsViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.calendarMonth.setDateTextAppearance(R.color.calendar_text_color)
        binding.calendarMonth.setOnDateChangedListener { _, date, _ ->
            val selectedDate = LocalDate.of(date.year, date.month, date.day)
            findNavController().navigate(
                DailyFragmentDirections.actionMainDailyToDayStatisticsDetailFragment(
                    selectedDate
                )
            )
        }
        val currentDate = binding.calendarMonth.currentDate
        viewModel.getUserMonthlyStatistic(currentDate.year, currentDate.month)

        binding.calendarMonth.setOnMonthChangedListener { _, date ->
            viewModel.getUserMonthlyStatistic(date.year, date.month)
        }

        viewModel.monthlyStatistics.observe(viewLifecycleOwner, {
            binding.layoutMonthCount.setBodyText(it.count.toString())
            binding.layoutMonthTime.setBodyText(it.time.toString())
            binding.layoutMonthStep.setBodyText(it.step)
        })

        viewModel.calendarDayList.observe(viewLifecycleOwner, {
            binding.calendarMonth.addDecorator(
                EventDecorator(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.pink,
                        null
                    ), it
                )
            )
        })
    }
}