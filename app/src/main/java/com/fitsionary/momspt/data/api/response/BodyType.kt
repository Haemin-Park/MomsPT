package com.fitsionary.momspt.data.api.response


import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@Parcelize
data class BodyType(
    val explanation: String,
    val id: Int,
    val name: String
) : Parcelable