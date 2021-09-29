package com.fitsionary.momspt.presentation.workoutdetail.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.fitsionary.momspt.database.getDatabase
import com.fitsionary.momspt.presentation.base.BaseAndroidViewModel
import com.fitsionary.momspt.repository.WorkoutPoseLandmarkRepository
import com.fitsionary.momspt.util.Event
import kotlinx.coroutines.launch

class WorkoutDetailViewModel(application: Application) : BaseAndroidViewModel(application) {
    private val database = getDatabase(application)
    private val workoutPoseLandmarkRepository =
        WorkoutPoseLandmarkRepository(application, database)

    val loadingStatus: LiveData<Boolean> = workoutPoseLandmarkRepository.isLoading
    val ableNavigation: LiveData<Event<Boolean>> = workoutPoseLandmarkRepository.ableNavigation

    fun downloadLandmarks() {
        viewModelScope.launch {
            workoutPoseLandmarkRepository.testRefreshData()
        }
//        addDisposable(
//            workoutPoseLandmarkRepository.refreshData()
//        )
    }
}