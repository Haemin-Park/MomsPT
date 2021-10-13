package com.fitsionary.momspt.data.api.response


import android.os.Parcelable
import com.fitsionary.momspt.data.model.TodayStatisticsModel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TodayStatisticsResponse(
    val bodyType: List<BodyType>,
    val totalKcal: Int,
    val totalTime: Int,
    val weightNow: Double
) : Parcelable

fun TodayStatisticsResponse.toModel(): TodayStatisticsModel {
    var bodyTypeToString = ""
    for (i in 0 until bodyType.size - 1) {
        bodyTypeToString += "${bodyType[i].name}, "
    }
    bodyTypeToString += bodyType.last().name

    return TodayStatisticsModel(
        bodyType = bodyTypeToString,
        totalKcal = totalKcal,
        totalTime = totalTime,
        weightNow = weightNow
    )
}