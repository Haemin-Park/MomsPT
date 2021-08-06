package com.fitsionary.momspt.presentation.workout.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fitsionary.momspt.data.api.request.TodayWorkoutListRequest
import com.fitsionary.momspt.data.api.response.TodayCommentResponse
import com.fitsionary.momspt.data.api.response.toModel
import com.fitsionary.momspt.data.model.WorkoutModel
import com.fitsionary.momspt.network.NetworkService
import com.fitsionary.momspt.presentation.base.BaseViewModel
import com.fitsionary.momspt.presentation.home.viewmodel.HomeViewModel
import com.fitsionary.momspt.util.rx.applyNetworkScheduler

class WorkoutViewModel : BaseViewModel() {
    private val _comment = MutableLiveData<TodayCommentResponse>()
    private val _workoutList = MutableLiveData<List<WorkoutModel>>()

    val comment: LiveData<TodayCommentResponse>
        get() = _comment
    val workoutList: LiveData<List<WorkoutModel>>
        get() = _workoutList

    fun getTodayComment(name: String) {
        addDisposable(
            NetworkService.api.getTodayComment(name)
                .applyNetworkScheduler()
                .subscribe({
                    Log.i(TAG, it.toString())
                    _comment.value = it
                }, {
                    Log.i(TAG, it.toString())
                })
        )
    }

    fun getTodayWorkoutList(request: TodayWorkoutListRequest) {
        addDisposable(
            NetworkService.api.getTodayWorkoutList(request)
                .applyNetworkScheduler()
                .subscribe({
                    Log.i(TAG, it.toString())
                    _workoutList.value = it.map { response -> response.toModel() }
                }, {
                    Log.i(TAG, it.toString())
                })
        )
    }

    companion object {
        private val TAG = WorkoutViewModel::class.simpleName
    }
}