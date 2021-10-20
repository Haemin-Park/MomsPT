package com.fitsionary.momspt.data.api.response

import android.os.Parcelable
import com.fitsionary.momspt.data.model.DayStatisticsDetailModel
import com.fitsionary.momspt.util.TimeUtil
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DayStatisticsDetailResponse(
    val time: Int,
    val kcal: Int,
    val step: Int,
    val day: Int,
    val workout: List<WorkoutListResponseItem>
) : Parcelable

fun DayStatisticsDetailResponse.toModel(): DayStatisticsDetailModel = DayStatisticsDetailModel(
    time = TimeUtil.makeWorkoutTimeFormat(time),
    kcal = kcal,
    step = step,
    stepDay = day,
    workout = workout.map {
        it.toModel()
    }
)