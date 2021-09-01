package com.fitsionary.momspt.domain

import com.fitsionary.momspt.data.api.response.Landmark

data class WorkoutLandmarkDomainModel(
    val poseData: List<PostDataItem>
) {
    data class PostDataItem(
        val frame: Long,
        val landmarks: List<Landmark>
    )
}