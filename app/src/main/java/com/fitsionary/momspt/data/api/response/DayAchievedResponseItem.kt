package com.fitsionary.momspt.data.api.response


import android.os.Parcelable
import com.fitsionary.momspt.data.model.DayAchievedModel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DayAchievedResponseItem(
    val done: Int,
    val order: Int,
    val totalWorkout: Int
) : Parcelable

fun DayAchievedResponseItem.toModel(): DayAchievedModel {
    return DayAchievedModel(
        day = when (order) {
            1 -> "월"
            2 -> "화"
            3 -> "수"
            4 -> "목"
            5 -> "금"
            6 -> "토"
            else -> "일"
        },
        successCount = done,
        entireCount = totalWorkout,
        isSuccess = totalWorkout != 0 && done == totalWorkout
    )
}