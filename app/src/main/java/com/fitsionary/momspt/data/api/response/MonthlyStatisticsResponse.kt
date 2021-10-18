package com.fitsionary.momspt.data.api.response


import android.os.Parcelable
import com.fitsionary.momspt.data.model.MonthlyStatisticsModel
import com.fitsionary.momspt.util.TimeUtil
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MonthlyStatisticsResponse(
    val count: Int,
    val monthlyStatistics: List<MonthlyStatisticsItem>,
    val step: String,
    val time: Int
) : Parcelable

fun MonthlyStatisticsResponse.toModel(year: Int, month: Int): MonthlyStatisticsModel {
    return MonthlyStatisticsModel(
        year = year,
        month = month,
        count = count,
        monthlyStatistics = monthlyStatistics,
        step = step,
        time = TimeUtil.makeWorkoutTimeFormat(time)
    )
}