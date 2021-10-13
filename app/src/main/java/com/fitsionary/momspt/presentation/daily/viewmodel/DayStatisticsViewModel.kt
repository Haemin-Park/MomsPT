package com.fitsionary.momspt.presentation.daily.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fitsionary.momspt.data.api.response.TodayStatisticsResponse
import com.fitsionary.momspt.network.NetworkService
import com.fitsionary.momspt.presentation.base.BaseViewModel
import com.fitsionary.momspt.util.rx.applyNetworkScheduler
import timber.log.Timber

class DayStatisticsViewModel : BaseViewModel() {
    private val _todayStatistics = MutableLiveData<TodayStatisticsResponse>()
    val todayStatistics: LiveData<TodayStatisticsResponse>
        get() = _todayStatistics

    fun getTodayStatisticsList() {
        NetworkService.api.getTodayStatistics()
            .applyNetworkScheduler()
            .subscribe({
                Timber.i(it.toString())
                _todayStatistics.value = it
            }, {
                Timber.e(it.message)
            })
    }
}