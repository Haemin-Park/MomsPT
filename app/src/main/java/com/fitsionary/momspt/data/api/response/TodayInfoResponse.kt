package com.fitsionary.momspt.data.api.response


import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
data class TodayInfoResponse(
    val dayAfterBabyDue: Int,
    val step: Int,
    val day: Int,
    val comment: String
) : Parcelable