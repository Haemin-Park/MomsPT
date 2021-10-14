package com.fitsionary.momspt.data.api.response

data class WorkoutResultResponse(
    val success: Boolean,
    val message: String,
    val nextWorkout: WorkoutListResponseItem?
)