package com.fitsionary.momspt.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DayStatisticsDetailModel(
    val time: Int,
    val kcal: Int,
    val workout: List<WorkoutModel>
) : Parcelable