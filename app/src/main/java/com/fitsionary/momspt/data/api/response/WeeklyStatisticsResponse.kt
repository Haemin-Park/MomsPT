package com.fitsionary.momspt.data.api.response

import com.fitsionary.momspt.data.model.WeeklyStatisticsModel
import com.fitsionary.momspt.util.TimeUtil


class WeeklyStatisticsResponse : ArrayList<WeeklyStatisticsResponseItem>()

fun WeeklyStatisticsResponse.toModel(): WeeklyStatisticsModel {
    val workoutTimeList = arrayListOf<Int>()
    val weightList = arrayListOf<Double>()
    this.map {
        workoutTimeList.add(TimeUtil.makeWorkoutTimeFormat(it.workoutTime))
        weightList.add(it.weight)
    }
    return WeeklyStatisticsModel(
        workoutTimeList = workoutTimeList,
        weightList = weightList
    )
}