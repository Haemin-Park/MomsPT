package com.fitsionary.momspt.data.model

import android.os.Parcelable
import com.fitsionary.momspt.data.api.response.MonthlyStatisticsItem
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MonthlyStatisticsModel(
    val year: Int,
    val month: Int,
    val count: Int,
    val monthlyStatistics: List<MonthlyStatisticsItem>,
    val step: String,
    val time: Int
) : Parcelable