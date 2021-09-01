package com.fitsionary.momspt.repository

import android.content.Context
import com.fitsionary.momspt.data.api.response.Landmark
import com.fitsionary.momspt.data.api.response.WorkoutPoseLandmarkResponse
import com.fitsionary.momspt.data.api.response.asDatabaseModel
import com.fitsionary.momspt.database.WorkoutLandmarkDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class WorkoutPoseLandmarkRepository(
    /**
     * 테스트 함수에 필요한 context
     */
    private val context: Context,
    private val database: WorkoutLandmarkDatabase,
) {

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

    suspend fun refreshData() {
//        NetworkService.api.getWorkoutPoseLandmark()
//            .applyNetworkScheduler()
//            .subscribe({
//                database.dao.insertWorkout(*db.workoutDBModel)
//                database.dao.insertLandmark(*db.landmarkDBModel)
//            }, {})

        withContext(Dispatchers.IO) {
            val db = readJson().asDatabaseModel()
            database.dao.insertWorkout(*db.workoutDBModel)
            database.dao.insertLandmark(*db.landmarkDBModel)
        }
    }

}