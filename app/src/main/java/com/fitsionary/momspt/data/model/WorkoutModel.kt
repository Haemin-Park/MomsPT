package com.fitsionary.momspt.data.model

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
data class WorkoutModel(
    val calorie: Int,
    val effect: String,
    val explanation: String,
    val name: String,
    val playtime: Int,
    val thumbnail: String,
    val type: String,
    val isFinish : Boolean
) : Parcelable