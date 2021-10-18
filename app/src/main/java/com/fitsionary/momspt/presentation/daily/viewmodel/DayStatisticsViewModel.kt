package com.fitsionary.momspt.presentation.daily.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fitsionary.momspt.data.api.request.EditWeightRequest
import com.fitsionary.momspt.data.api.response.toModel
import com.fitsionary.momspt.data.model.TodayStatisticsModel
import com.fitsionary.momspt.data.model.WeeklyStatisticsModel
import com.fitsionary.momspt.network.NetworkService
import com.fitsionary.momspt.presentation.base.BaseViewModel
import com.fitsionary.momspt.util.rx.applyNetworkScheduler
import timber.log.Timber

class DayStatisticsViewModel : BaseViewModel() {
    private val _todayStatistics = MutableLiveData<TodayStatisticsModel>()
    val todayStatistics: LiveData<TodayStatisticsModel>
        get() = _todayStatistics

    private val _weight = MutableLiveData<Double>()
    val weight: LiveData<Double>
        get() = _weight

    private val _weeklyStatistics = MutableLiveData<WeeklyStatisticsModel>()
    val weeklyStatistics: LiveData<WeeklyStatisticsModel>
        get() = _weeklyStatistics

    fun getTodayUserStatistics() {
        NetworkService.api.getTodayStatistics()
            .applyNetworkScheduler()
            .subscribe({
                Timber.i(it.toString())
                _todayStatistics.value = it.toModel()
                updateWeight(_todayStatistics.value!!.weightNow)
            }, {
                Timber.e(it.message)
            })
    }

    fun editTodayUserWeight(weight: Double) {
        NetworkService.api.editWeight(
            EditWeightRequest(weight)
        ).applyNetworkScheduler()
            .subscribe({
                Timber.i(it.toString())
                if (it.success)
                    updateWeight(weight)
            }, {
                Timber.e(it.message)
            })
    }

    fun getWeeklyUserStatistics() {
        NetworkService.api.getWeeklyStatistics()
            .applyNetworkScheduler()
            .subscribe({
                _weeklyStatistics.value = it.toModel()
                Timber.i(it.toString())
            }, {
                Timber.e(it.message)
            })
    }

    private fun updateWeight(weight: Double) {
        _weight.value = weight
    }
}