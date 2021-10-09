package com.fitsionary.momspt.presentation.workoutdetail.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fitsionary.momspt.database.getDatabase
import com.fitsionary.momspt.presentation.base.BaseAndroidViewModel
import com.fitsionary.momspt.repository.WorkoutPoseLandmarkRepository
import com.fitsionary.momspt.util.Event
import kotlinx.coroutines.launch

class WorkoutDetailViewModel(application: Application, val workoutCode: String) :
    BaseAndroidViewModel(application) {
    private val database = getDatabase(application)
    private val workoutPoseLandmarkRepository =
        WorkoutPoseLandmarkRepository(database)

    val isAlreadyExistWorkout: LiveData<Boolean> =
        workoutPoseLandmarkRepository.isAlreadyExistWorkout(workoutCode)
    val loadingStatus: LiveData<Boolean> = workoutPoseLandmarkRepository.isLoading
    val ableNavigation: LiveData<Event<Boolean>> = workoutPoseLandmarkRepository.ableNavigation

    fun downloadLandmarks() {
        viewModelScope.launch {
            workoutPoseLandmarkRepository.refreshData(workoutCode)
        }
    }

    class ViewModelFactory(private val application: Application, private val workoutCode: String) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(WorkoutDetailViewModel::class.java)) {
                WorkoutDetailViewModel(application, workoutCode) as T
            } else {
                throw IllegalArgumentException()
            }
        }
    }
}