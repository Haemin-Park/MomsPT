package com.fitsionary.momspt.data.api.response


import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@Parcelize
data class History(
    val pause_time: Int,
    val score: String
) : Parcelable