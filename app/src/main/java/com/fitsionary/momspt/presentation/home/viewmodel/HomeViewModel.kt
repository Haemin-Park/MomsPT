package com.fitsionary.momspt.presentation.home.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fitsionary.momspt.data.api.response.TodayInfoResponse
import com.fitsionary.momspt.data.api.response.toModel
import com.fitsionary.momspt.data.model.DayAchievedModel
import com.fitsionary.momspt.data.model.WorkoutModel
import com.fitsionary.momspt.network.NetworkService
import com.fitsionary.momspt.presentation.base.BaseAndroidViewModel
import com.fitsionary.momspt.util.rx.applyNetworkScheduler
import timber.log.Timber

class HomeViewModel(application: Application) : BaseAndroidViewModel(application) {
    private val _info = MutableLiveData<TodayInfoResponse>()
    val info: LiveData<TodayInfoResponse>
        get() = _info

    private val _workoutList = MutableLiveData<List<WorkoutModel>>()
    val workoutList: LiveData<List<WorkoutModel>>
        get() = _workoutList

    private val _dayAchievedList = MutableLiveData<List<DayAchievedModel>>()
    val dayAchievedList: LiveData<List<DayAchievedModel>>
        get() = _dayAchievedList

    fun getTodayInfo() {
        addDisposable(
            NetworkService.api.getTodayInfo()
                .applyNetworkScheduler()
                .subscribe({
                    Timber.i(it.toString())
                    _info.value = it
                }, {
                    Timber.e(it.message!!)
                })
        )
    }

    fun getTodayWorkoutList() {
        addDisposable(
            NetworkService.api.getTodayWorkoutList()
                .applyNetworkScheduler()
                .subscribe({
                    Timber.i(it.toString())
                    _workoutList.value = it.map { response -> response.toModel() }
                }, {
                    Timber.e(it.message!!)
                })
        )
    }

    fun getWeeklyAchieved() {
        addDisposable(
            NetworkService.api.getWeeklyAchieved()
                .applyNetworkScheduler()
                .subscribe({
                    Timber.i(it.toString())
                    _dayAchievedList.value = it.map { response -> response.toModel() }
                }, {
                    Timber.e(it.message!!)
                })
        )
    }
}