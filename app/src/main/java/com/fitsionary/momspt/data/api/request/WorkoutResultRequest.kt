package com.fitsionary.momspt.data.api.request


import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@Parcelize
data class WorkoutResultRequest(
    val date: String,
    val score: String,
    val workout_id: Int
) : Parcelable