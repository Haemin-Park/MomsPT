package com.fitsionary.momspt.data.api.request

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DayStatisticsDetailRequest(
    val date: String
) : Parcelable