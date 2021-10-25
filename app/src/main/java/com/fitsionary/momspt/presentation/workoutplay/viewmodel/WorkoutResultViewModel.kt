package com.fitsionary.momspt.presentation.workoutplay.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.fitsionary.momspt.data.api.request.WorkoutResultRequest
import com.fitsionary.momspt.data.api.response.toModel
import com.fitsionary.momspt.data.model.WorkoutModel
import com.fitsionary.momspt.database.getDatabase
import com.fitsionary.momspt.network.NetworkService
import com.fitsionary.momspt.presentation.base.BaseAndroidViewModel
import com.fitsionary.momspt.repository.WorkoutPoseLandmarkRepository
import com.fitsionary.momspt.util.rx.applyNetworkScheduler
import timber.log.Timber

class WorkoutResultViewModel(application: Application) : BaseAndroidViewModel(application) {
    private val _nextWorkout = MutableLiveData<WorkoutModel>()
    val nextWorkout: LiveData<WorkoutModel>
        get() = _nextWorkout

    private val database = getDatabase(application)
    private val workoutPoseLandmarkRepository =
        WorkoutPoseLandmarkRepository(database)

    val isAlreadyExistWorkout: LiveData<Boolean> = Transformations.switchMap(nextWorkout) {
        workoutPoseLandmarkRepository.isAlreadyExistWorkout(it.workoutCode)
    }

    fun sendWorkoutResult(id: Int, date: String, rank: String) {
        addDisposable(
            NetworkService.api.sendWorkoutResult(
                WorkoutResultRequest(
                    date, rank, id
                )
            ).applyNetworkScheduler()
                .subscribe({
                    Timber.i(it.toString())
                    if (it.success) {
                        if (it.nextWorkout != null) {
                            _nextWorkout.value = it.nextWorkout.toModel()
                        }
                    }
                }, {
                    Timber.e(it.message)
                })
        )
    }

    class ViewModelFactory(private val application: Application) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(WorkoutResultViewModel::class.java)) {
                WorkoutResultViewModel(application) as T
            } else {
                throw IllegalArgumentException()
            }
        }
    }
}