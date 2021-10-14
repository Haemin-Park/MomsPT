package com.fitsionary.momspt.presentation.workoutplay.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fitsionary.momspt.data.api.request.WorkoutResultRequest
import com.fitsionary.momspt.data.api.response.toModel
import com.fitsionary.momspt.data.model.WorkoutModel
import com.fitsionary.momspt.network.NetworkService
import com.fitsionary.momspt.presentation.base.BaseViewModel
import com.fitsionary.momspt.util.rx.applyNetworkScheduler
import timber.log.Timber

class WorkoutResultViewModel : BaseViewModel() {
    private val _nextWorkout = MutableLiveData<WorkoutModel>()
    val nextWorkout: LiveData<WorkoutModel>
        get() = _nextWorkout

    fun sendWorkoutResult(id: Int, date: String, rank: String) {
        NetworkService.api.sendWorkoutResult(
            WorkoutResultRequest(
                date, rank, id
            )
        ).applyNetworkScheduler()
            .subscribe({
                Timber.i(it.toString())
                if (it.success)
                    _nextWorkout.value = it.nextWorkout?.toModel()
            }, {
                Timber.e(it.message)
            })
    }
}