package com.fitsionary.momspt.data.api.response


import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@Parcelize
data class User(
    val babyDue: String,
    val heightNow: Double,
    val kakaoId: Int,
    val nickname: String,
    val weightBeforePregnancy: Double,
    val weightNow: Double
) : Parcelable