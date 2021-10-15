package com.fitsionary.momspt.presentation.workout.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fitsionary.momspt.data.api.response.TodayInfoResponse
import com.fitsionary.momspt.data.api.response.toModel
import com.fitsionary.momspt.data.model.WorkoutModel
import com.fitsionary.momspt.network.NetworkService
import com.fitsionary.momspt.presentation.base.BaseViewModel
import com.fitsionary.momspt.util.rx.applyNetworkScheduler
import timber.log.Timber

class WorkoutViewModel : BaseViewModel() {
    private val _info = MutableLiveData<TodayInfoResponse>()
    val info: LiveData<TodayInfoResponse>
        get() = _info

    private val _workoutList = MutableLiveData<List<WorkoutModel>>()
    val workoutList: LiveData<List<WorkoutModel>>
        get() = _workoutList

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
                    Timber.e(it.message)
                })
        )
    }
}