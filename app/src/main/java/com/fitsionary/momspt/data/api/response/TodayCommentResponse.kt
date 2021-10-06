package com.fitsionary.momspt.data.api.response


import android.annotation.SuppressLint
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class TodayCommentResponse(
    val dayAfterBabyDue: Int,
    val comment: String
) : Parcelable