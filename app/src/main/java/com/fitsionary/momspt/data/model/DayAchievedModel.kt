package com.fitsionary.momspt.data.model

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
data class DayAchievedModel(
    val day: String,
    val successCount: Int,
    val entireCount: Int,
    val isSuccess: Boolean
) : Parcelable

fun getTestDayAchievedData() =
    listOf(
        DayAchievedModel("월", 0, 3, false),
        DayAchievedModel("화", 3, 3, true),
        DayAchievedModel("수", 1, 3, false),
        DayAchievedModel("목", 1, 1, true),
        DayAchievedModel("금", 1, 1, true),
        DayAchievedModel("토", 1, 1, true),
        DayAchievedModel("일", 1, 1, true)
    )


