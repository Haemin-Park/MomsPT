package com.fitsionary.momspt.data.api.response

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
data class Landmark(
    val part: String,
    val x: Double,
    val y: Double,
    val z: Double,
    val visibility: Double
) : Parcelable