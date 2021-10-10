package com.fitsionary.momspt.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DayAchievedModel(
    val day: String,
    val successCount: Int,
    val entireCount: Int,
    val isSuccess: Boolean
) : Parcelable

