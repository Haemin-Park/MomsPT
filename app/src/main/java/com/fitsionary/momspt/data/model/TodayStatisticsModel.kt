package com.fitsionary.momspt.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TodayStatisticsModel(
    val bodyType: String,
    val totalKcal: Int,
    val totalTime: Int,
    val weightNow: Double
) : Parcelable