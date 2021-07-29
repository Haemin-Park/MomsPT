package com.fitsionary.momspt.data.api.request

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
data class Pose(
    val part: String,
    val position: Position,
    val visibility: Float
) : Parcelable