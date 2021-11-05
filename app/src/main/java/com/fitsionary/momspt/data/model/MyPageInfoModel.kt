package com.fitsionary.momspt.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MyPageInfoModel(
    var babyName: String,
    val dayAfterBabyDue: Int,
    var nickname: String,
    var thumbnail: String?
) : Parcelable