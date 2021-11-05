package com.fitsionary.momspt.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BodyAnalysisResultModel(
    val bodyComment: String,
    val workoutComment: String,
    val filePath: String
) : Parcelable