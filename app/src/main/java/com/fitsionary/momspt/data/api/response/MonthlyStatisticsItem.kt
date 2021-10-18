package com.fitsionary.momspt.data.api.response


import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MonthlyStatisticsItem(
    val day: Int,
    val status: String
) : Parcelable