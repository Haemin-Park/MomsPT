package com.fitsionary.momspt.data.api.response

import com.fitsionary.momspt.database.DatabaseLandmark
import com.fitsionary.momspt.database.DatabaseWorkout

data class WorkoutPoseLandmarkResponse(
    val workout: String,
    val poseData: List<PostDataItem>
) {
    data class PostDataItem(
        val frame: Long,
        val landmarks: List<Landmark>
    )
}

fun WorkoutPoseLandmarkResponse.asDatabaseModel(): Result {
    val databaseWorkout = mutableListOf<DatabaseWorkout>()
    val databaseLandmark = mutableListOf<DatabaseLandmark>()

    this.poseData.map {
        val landmarkId = workout + "_" + it.frame
        databaseWorkout.add(
            DatabaseWorkout(
                workoutName = workout,
                frameNum = it.frame,
                childLandmarkId = landmarkId
            )
        )
        it.landmarks.map { landmark ->
            databaseLandmark.add(
                DatabaseLandmark(
                    landmarkId = landmarkId,
                    part = landmark.part,
                    x = landmark.x,
                    y = landmark.y,
                    z = landmark.z,
                    visibility = landmark.visibility
                )
            )
        }
    }

    return Result(databaseWorkout.toTypedArray(), databaseLandmark.toTypedArray())
}

data class Result(
    val workoutDBModel: Array<DatabaseWorkout>,
    val landmarkDBModel: Array<DatabaseLandmark>,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Result

        if (!workoutDBModel.contentEquals(other.workoutDBModel)) return false
        if (!landmarkDBModel.contentEquals(other.landmarkDBModel)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = workoutDBModel.contentHashCode()
        result = 31 * result + landmarkDBModel.contentHashCode()
        return result
    }
}