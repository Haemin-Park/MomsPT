package com.fitsionary.momspt.presentation.daily.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fitsionary.momspt.data.api.request.DayStatisticsDetailRequest
import com.fitsionary.momspt.data.api.response.toModel
import com.fitsionary.momspt.data.model.DayStatisticsDetailModel
import com.fitsionary.momspt.network.NetworkService
import com.fitsionary.momspt.presentation.base.BaseViewModel
import com.fitsionary.momspt.util.rx.applyNetworkScheduler
import timber.log.Timber

class DayStatisticsDetailViewModel : BaseViewModel() {
    private val _dayStatistics = MutableLiveData<DayStatisticsDetailModel>()
    val dayStatistics: LiveData<DayStatisticsDetailModel>
        get() = _dayStatistics

    fun getDayStatisticsDetail(date: String) {
        addDisposable(
            NetworkService.api.getDayStatisticsDetail(DayStatisticsDetailRequest(date))
                .applyNetworkScheduler()
                .subscribe({
                    Timber.i(it.toString())
                    _dayStatistics.value = it.toModel()
                }, {})
        )
    }
}