package com.fitsionary.momspt.data.model

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
data class WorkoutModel(
    val workoutCode: String,
    val calorie: Int,
    val effect: String,
    val explanation: String,
    val name: String,
    val playtime: Int,
    val thumbnail: String,
    val video: String,
    val type: List<String>,
    val isFinish: Boolean,
    val rank: String
) : Parcelable