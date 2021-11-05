package com.fitsionary.momspt.data.api.response


import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@Parcelize
data class BodyAnalysisResultResponse(
    val bodyComment: String,
    val glbURL: String?,
    val workoutComment: String
) : Parcelable