package com.fitsionary.momspt.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class WeeklyStatisticsModel(
    val workoutTimeList: ArrayList<Int>,
    val weightList: ArrayList<Double>
) : Parcelable