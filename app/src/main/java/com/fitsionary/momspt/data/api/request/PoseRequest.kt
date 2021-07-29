package com.fitsionary.momspt.data.api.request

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
data class PoseRequest(
    val name: String,
    val height: Int,
    val width: Int,
    val timestamp: Long,
    val pose: List<Pose>,
) : Parcelable