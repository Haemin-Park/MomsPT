package com.fitsionary.momspt.data.api.response


import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@Parcelize
data class VideoCheckTime(
    val workoutFinishTime: Int,
    val workoutStartTime: Int
) : Parcelable