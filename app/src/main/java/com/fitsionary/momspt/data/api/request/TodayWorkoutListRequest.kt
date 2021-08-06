package com.fitsionary.momspt.data.api.request


import android.annotation.SuppressLint
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class TodayWorkoutListRequest(
    val date: String,
    val name: String
) : Parcelable