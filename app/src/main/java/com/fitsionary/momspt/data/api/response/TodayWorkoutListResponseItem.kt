package com.fitsionary.momspt.data.api.response


import android.annotation.SuppressLint
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable
import com.fitsionary.momspt.S3_URL
import com.fitsionary.momspt.data.model.WorkoutModel
import com.fitsionary.momspt.util.TimeUtil

@SuppressLint("ParcelCreator")
@Parcelize
data class TodayWorkoutListResponseItem(
    val set_id: Int,
    val workout: Workout,
    val workout_id: Int
) : Parcelable

fun TodayWorkoutListResponseItem.toModel() : WorkoutModel {
    return WorkoutModel(
        calorie = workout.calorie,
        effect = workout.effect,
        explanation = workout.explanation,
        name = workout.name,
        playtime = TimeUtil.makeWorkoutTimeFormat(workout.playtime),
        thumbnail = workout.thumbnail,
        type = workout.type,
        isFinish = true
    )
}