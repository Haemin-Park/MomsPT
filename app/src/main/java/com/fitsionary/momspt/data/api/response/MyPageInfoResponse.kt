package com.fitsionary.momspt.data.api.response


import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@Parcelize
data class MyPageInfoResponse(
    var babyName: String,
    val dayAfterBabyDue: Int,
    var nickname: String,
    var thumbnail: String
) : Parcelable