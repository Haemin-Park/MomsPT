package com.fitsionary.momspt.database

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.fitsionary.momspt.data.api.response.Landmark
import com.fitsionary.momspt.domain.WorkoutLandmarkDomainModel

/**
 * workout
 */
@Entity(tableName = "workouts")
data class DatabaseWorkout(
    val workoutCode: String,
    val frameNum: Long,
    @PrimaryKey
    val childLandmarkId: String // workoutCode_frameNum
)

/**
 * landmark
 */
@Entity(tableName = "landmarks")
data class DatabaseLandmark(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val landmarkId: String,
    val part: String,
    val x: Double,
    val y: Double,
    val z: Double,
    val visibility: Double
)

/**
 * workout - landmark
 */
@Entity
data class DatabaseWorkoutWithLandmark(
    @Embedded
    val workout: DatabaseWorkout,
    @Relation(
        parentColumn = "childLandmarkId",
        entityColumn = "landmarkId",
    )
    val landmarks: List<DatabaseLandmark>
)

fun List<DatabaseWorkoutWithLandmark>.asDomainModel(): WorkoutLandmarkDomainModel {
    return WorkoutLandmarkDomainModel(
        poseData = this.map {
            WorkoutLandmarkDomainModel.PoseDataItem(
                it.workout.frameNum,
                it.landmarks.map { landmark ->
                    Landmark(landmark.part, landmark.x, landmark.y, landmark.z, landmark.visibility)
                }
            )
        }
    )
}