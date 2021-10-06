package com.fitsionary.momspt.data.api.response


import android.os.Parcelable
import com.fitsionary.momspt.data.model.WorkoutModel
import com.fitsionary.momspt.util.NONE
import com.fitsionary.momspt.util.TimeUtil
import kotlinx.android.parcel.Parcelize

@Parcelize
data class WorkoutListResponseItem(
    val workoutCode: String,
    val calorie: Int,
    val effect: List<String>,
    val explanation: String,
    val history: History?,
    val id: Int,
    val name: String,
    val playtime: Int,
    val thumbnail: String,
    val video: String,
    val type: List<String>,
    val videoCheckTime: List<VideoCheckTime>,
    val videoCode: String,

    ) : Parcelable

fun WorkoutListResponseItem.toModel(): WorkoutModel {
    return WorkoutModel(
        workoutCode = workoutCode,
        calorie = calorie,
        effect = effect.toString().let {
            it.substring(1, it.length - 1)
        },
        explanation = explanation,
        name = name,
        playtime = TimeUtil.makeWorkoutTimeFormat(playtime),
        thumbnail = thumbnail,
        video = video,
        type = type,
        isFinish = history != null,
        rank = history?.score ?: NONE
    )
}