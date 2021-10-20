package com.fitsionary.momspt.data.api.request

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StepWorkoutRequest(
    val step: Int,
    val day: Int
) : Parcelable