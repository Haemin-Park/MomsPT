package com.fitsionary.momspt.presentation.daily.view

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.fitsionary.momspt.R
import com.fitsionary.momspt.data.enum.DirectionEnum
import com.fitsionary.momspt.databinding.FragmentDayStatisticsBinding
import com.fitsionary.momspt.presentation.base.BaseFragment
import com.fitsionary.momspt.presentation.daily.viewmodel.DayStatisticsViewModel

class DayStatisticsFragment :
    BaseFragment<FragmentDayStatisticsBinding, DayStatisticsViewModel>(R.layout.fragment_day_statistics) {
    override val viewModel: DayStatisticsViewModel by lazy {
        ViewModelProvider(this).get(DayStatisticsViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvAnalysis.setOnClickListener {
            findNavController().navigate(
                DailyFragmentDirections.actionMainDailyToAnalysisFragment(
                    DirectionEnum.TO_DAILY
                )
            )
        }
    }
}