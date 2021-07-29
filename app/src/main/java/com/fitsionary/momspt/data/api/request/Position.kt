package com.fitsionary.momspt.data.api.request

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
data class Position(
    val x: Float,
    val y: Float,
    val z: Float
) : Parcelable