package com.fitsionary.momspt.presentation.daily.view

import androidx.lifecycle.ViewModelProvider
import com.fitsionary.momspt.R
import com.fitsionary.momspt.databinding.FragmentDayStatisticsBinding
import com.fitsionary.momspt.presentation.base.BaseFragment
import com.fitsionary.momspt.presentation.daily.viewmodel.DayStatisticsViewModel

class DayStatisticsFragment :
    BaseFragment<FragmentDayStatisticsBinding, DayStatisticsViewModel>(R.layout.fragment_day_statistics) {
    override val viewModel: DayStatisticsViewModel by lazy {
        ViewModelProvider(this).get(DayStatisticsViewModel::class.java)
    }
}