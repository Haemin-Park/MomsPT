package com.fitsionary.momspt.presentation.home.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fitsionary.momspt.data.api.request.TodayWorkoutListRequest
import com.fitsionary.momspt.data.api.response.TodayCommentResponse
import com.fitsionary.momspt.data.api.response.toModel
import com.fitsionary.momspt.data.model.DayAchievedModel
import com.fitsionary.momspt.data.model.WorkoutModel
import com.fitsionary.momspt.data.model.getTestData
import com.fitsionary.momspt.data.model.getTestDayAchievedData
import com.fitsionary.momspt.network.NetworkService
import com.fitsionary.momspt.presentation.base.BaseAndroidViewModel
import com.fitsionary.momspt.util.rx.applyNetworkScheduler
import timber.log.Timber

class HomeViewModel(application: Application) : BaseAndroidViewModel(application) {
    private val _comment = MutableLiveData<TodayCommentResponse>()
    val comment: LiveData<TodayCommentResponse>
        get() = _comment

    private val _workoutList = MutableLiveData<List<WorkoutModel>>()
    val workoutList: LiveData<List<WorkoutModel>>
        get() = _workoutList

    private val _dayAchievedList = MutableLiveData<List<DayAchievedModel>>()
    val dayAchievedList: LiveData<List<DayAchievedModel>>
        get() = _dayAchievedList

    init {
        _workoutList.value = getTestData()
        _dayAchievedList.value = getTestDayAchievedData()
    }

    fun getTodayComment(name: String) {
        addDisposable(
            NetworkService.api.getTodayComment(name)
                .applyNetworkScheduler()
                .subscribe({
                    Timber.i(it.toString())
                    _comment.value = it
                }, {
                    Timber.e(it.message!!)
                })
        )
    }

    fun getTodayWorkoutList(request: TodayWorkoutListRequest) {
        addDisposable(
            NetworkService.api.getTodayWorkoutList(request)
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