package com.fitsionary.momspt.data.api.response


import android.annotation.SuppressLint
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class TodayCommentResponse(
    val comment: String,
    val d_day: Int
) : Parcelable