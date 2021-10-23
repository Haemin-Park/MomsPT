package com.fitsionary.momspt.data.api.response


import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@Parcelize
data class VideoCheckTime(
    val workoutFinishTime: Long,
    val workoutStartTime: Long
) : Parcelable