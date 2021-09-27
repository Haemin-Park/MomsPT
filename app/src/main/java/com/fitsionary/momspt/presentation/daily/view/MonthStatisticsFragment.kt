package com.fitsionary.momspt.presentation.daily.view

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.fitsionary.momspt.R
import com.fitsionary.momspt.databinding.FragmentMonthStatisticsBinding
import com.fitsionary.momspt.presentation.base.BaseFragment
import com.fitsionary.momspt.presentation.daily.viewmodel.MonthStatisticsViewModel


class MonthStatisticsFragment :
    BaseFragment<FragmentMonthStatisticsBinding, MonthStatisticsViewModel>(
        R.layout.fragment_month_statistics
    ) {
    override val viewModel: MonthStatisticsViewModel by lazy {
        ViewModelProvider(this).get(MonthStatisticsViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.calendarMonth.setOnDateChangeListener { view, year, month, dayOfMonth ->
            findNavController().navigate(DailyFragmentDirections.actionMainDailyToDayStatisticsDetailFragment())
        }
    }
}