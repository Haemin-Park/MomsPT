package com.fitsionary.momspt.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.fitsionary.momspt.data.api.response.Landmark
import com.fitsionary.momspt.data.api.response.WorkoutPoseLandmarkResponse
import com.fitsionary.momspt.data.api.response.asDatabaseModel
import com.fitsionary.momspt.database.WorkoutLandmarkDatabase
import com.fitsionary.momspt.database.asDomainModel
import com.fitsionary.momspt.network.NetworkService
import com.fitsionary.momspt.util.Event
import com.fitsionary.momspt.util.rx.applyNetworkScheduler
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import timber.log.Timber

class WorkoutPoseLandmarkRepository(
    private val context: Context, // 테스트 함수에 필요한 context
    private val database: WorkoutLandmarkDatabase,
) {
    fun workoutLandmarks(workName: String) =
        Transformations.map(database.dao.getWorkoutWithLandmark(workName)) {
            Timber.i("$workName ${it.count()}")
            it.asDomainModel()
        }

    val isLoading = MutableLiveData(false)
    val ableNavigation = MutableLiveData(Event(false))

    /**
     * 테스트 함수
     */
    private fun readJson(): WorkoutPoseLandmarkResponse {
        val poseData = mutableListOf<WorkoutPoseLandmarkResponse.PostDataItem>()

        val assetManager = context.resources.assets
        val inputStream = assetManager.open("tabata.json")
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        val jObject = JSONObject(jsonString)
        val workout = jObject.getString("workout")
        val jArray = jObject.getJSONArray("posedata")

        for (i in 0 until jArray.length()) {
            val obj = jArray.getJSONObject(i)
            val frame = obj.getLong("frame")
            val landmarks = obj.getJSONArray("landmarks")
            val landmarkList = ArrayList<Landmark>()
            landmarkList.clear()
            for (j in 0 until landmarks.length()) {
                val landmark = landmarks.getJSONObject(j)
                val part = landmark.getString("part")
                val x = landmark.getDouble("x")
                val y = landmark.getDouble("y")
                val z = landmark.getDouble("z")
                val visibility = landmark.getDouble("visibility")
                landmarkList.add(Landmark(part, x, y, z, visibility))
            }
            poseData.add(WorkoutPoseLandmarkResponse.PostDataItem(frame, landmarkList))
        }
        return WorkoutPoseLandmarkResponse(workout, poseData)
    }

    suspend fun testRefreshData() {
        withContext(Dispatchers.IO) {
            isLoading.postValue(true)
            val db = readJson().asDatabaseModel()
            database.dao.insertWorkout(*db.workoutDBModel)
            database.dao.insertLandmark(*db.landmarkDBModel)
            isLoading.postValue(false)
            ableNavigation.postValue(Event(true))
        }
    }

    fun refreshData(): Disposable =
        NetworkService.api.getWorkoutPoseLandmark()
            .applyNetworkScheduler()
            .doOnSubscribe { isLoading.value = true }
            .doAfterTerminate { isLoading.value = false }
            .subscribe({
                val db = it.asDatabaseModel()
                database.dao.insertWorkout(*db.workoutDBModel)
                database.dao.insertLandmark(*db.landmarkDBModel)
                ableNavigation.postValue(Event(true))
            }, {})

}