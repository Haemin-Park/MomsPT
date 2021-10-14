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
            1 -> "일"
            2 -> "월"
            3 -> "화"
            4 -> "수"
            5 -> "목"
            6 -> "금"
            else -> "토"
        },
        successCount = done,
        entireCount = totalWorkout,
        isSuccess = totalWorkout != 0 && done == totalWorkout
    )
}