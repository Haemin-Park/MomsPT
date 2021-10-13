package com.fitsionary.momspt.presentation.daily.view

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.fitsionary.momspt.R
import com.fitsionary.momspt.data.enum.DirectionEnum
import com.fitsionary.momspt.databinding.FragmentDayStatisticsBinding
import com.fitsionary.momspt.presentation.base.BaseFragment
import com.fitsionary.momspt.presentation.daily.viewmodel.DayStatisticsViewModel
import com.fitsionary.momspt.util.NavResult
import com.fitsionary.momspt.util.navResult
import timber.log.Timber


class DayStatisticsFragment :
    BaseFragment<FragmentDayStatisticsBinding, DayStatisticsViewModel>(R.layout.fragment_day_statistics) {
    override val viewModel: DayStatisticsViewModel by lazy {
        ViewModelProvider(this).get(DayStatisticsViewModel::class.java)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel

        navResult(findNavController()) { answer ->
            when (answer) {
                is NavResult.Cancel -> Timber.i("Cancel")
                is NavResult.Ok -> Timber.i("Ok")
                is NavResult.TodayWeight -> {
                    println(answer.weight)
                }
            }
        }

        viewModel.getTodayStatisticsList()
        binding.btnEditWeight.setOnClickListener {
            findNavController().navigate(DailyFragmentDirections.actionMainDailyToCustomEditTodayWeightDialog())
        }
        binding.tvAnalysis.setOnClickListener {
            findNavController().navigate(
                DailyFragmentDirections.actionMainDailyToAnalysisFragment(
                    DirectionEnum.TO_DAILY
                )
            )
        }
    }
}