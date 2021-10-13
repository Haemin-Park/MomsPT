package com.fitsionary.momspt.data.api.response


import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@Parcelize
data class TodayStatisticsResponse(
    val bodyType: List<BodyType>,
    val totalKcal: Int,
    val totalTime: Int,
    val weightNow: Double
) : Parcelable