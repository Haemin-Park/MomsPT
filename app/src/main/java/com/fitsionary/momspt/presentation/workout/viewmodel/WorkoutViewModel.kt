package com.fitsionary.momspt.presentation.workout.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fitsionary.momspt.data.api.response.TodayCommentResponse
import com.fitsionary.momspt.data.api.response.toModel
import com.fitsionary.momspt.data.model.WorkoutModel
import com.fitsionary.momspt.network.NetworkService
import com.fitsionary.momspt.presentation.base.BaseViewModel
import com.fitsionary.momspt.util.rx.applyNetworkScheduler
import timber.log.Timber

class WorkoutViewModel : BaseViewModel() {
    private val _comment = MutableLiveData<TodayCommentResponse>()
    val comment: LiveData<TodayCommentResponse>
        get() = _comment

    private val _workoutList = MutableLiveData<List<WorkoutModel>>()
    val workoutList: LiveData<List<WorkoutModel>>
        get() = _workoutList

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
}