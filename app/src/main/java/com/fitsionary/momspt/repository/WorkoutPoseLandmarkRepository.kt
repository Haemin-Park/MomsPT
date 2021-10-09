package com.fitsionary.momspt.repository

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.fitsionary.momspt.data.api.response.asDatabaseModel
import com.fitsionary.momspt.database.WorkoutLandmarkDatabase
import com.fitsionary.momspt.database.asDomainModel
import com.fitsionary.momspt.network.NetworkService
import com.fitsionary.momspt.util.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class WorkoutPoseLandmarkRepository(
    private val database: WorkoutLandmarkDatabase,
) {
    fun workoutLandmarks(workoutCode: String) =
        Transformations.map(database.dao.getWorkoutWithLandmark(workoutCode)) {
            Timber.i("$workoutCode ${it.count()}")
            it.asDomainModel()
        }

    fun isAlreadyExistWorkout(workoutCode: String) =
        Transformations.map(database.dao.getWorkout(workoutCode)) {
            it != null
        }

    val isLoading = MutableLiveData(false)
    val ableNavigation = MutableLiveData(Event(false))

    suspend fun refreshData(workoutCode: String) {
        withContext(Dispatchers.IO) {
            isLoading.postValue(true)
            val result = NetworkService.api.getWorkoutPoseLandmark(workoutCode)
            val db = result.asDatabaseModel()
            database.dao.insertWorkout(*db.workoutDBModel)
            database.dao.insertLandmark(*db.landmarkDBModel)
            isLoading.postValue(false)
            ableNavigation.postValue(Event(true))
        }
    }
}