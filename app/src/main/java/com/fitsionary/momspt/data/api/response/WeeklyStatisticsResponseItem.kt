package com.fitsionary.momspt.data.api.response


import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@Parcelize
data class WeeklyStatisticsResponseItem(
    val order: Int,
    val weight: Double,
    val workoutTime: Int
) : Parcelable