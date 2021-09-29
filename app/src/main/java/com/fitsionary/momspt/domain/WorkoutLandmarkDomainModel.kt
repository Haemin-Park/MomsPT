package com.fitsionary.momspt.domain

import android.annotation.SuppressLint
import android.os.Parcelable
import com.fitsionary.momspt.data.api.response.Landmark
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
data class WorkoutLandmarkDomainModel(
    val poseData: List<PostDataItem>
) : Parcelable {
    @Parcelize
    data class PostDataItem(
        val frame: Long,
        val landmarks: List<Landmark>
    ) : Parcelable
}