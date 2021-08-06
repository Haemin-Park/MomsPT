package com.fitsionary.momspt.data.api.response


import android.annotation.SuppressLint
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable
import com.fitsionary.momspt.S3_URL

@SuppressLint("ParcelCreator")
@Parcelize
data class Workout(
    val calorie: Int,
    val effect: String,
    val explanation: String,
    val name: String,
    val playtime: Int,
    val thumbnail: String,
    val type: String
) : Parcelable