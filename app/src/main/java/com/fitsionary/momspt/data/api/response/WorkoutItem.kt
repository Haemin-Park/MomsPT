package com.fitsionary.momspt.data.api.response

data class WorkoutItem(
    val thumbnail: String,
    val name: String,
    val time: Int,
    val kcal: Int,
    val isFinish: Boolean
)

fun getSampleRoutineItem() =
    mutableListOf(
        WorkoutItem("", "샘플운동1", 10, 100, true),
        WorkoutItem("", "샘플운동2", 20, 200, true),
        WorkoutItem("", "샘플운동3", 30, 300, false),
        WorkoutItem("", "샘플운동4", 40, 400, false),
        WorkoutItem("", "샘플운동5", 50, 500, false),
        WorkoutItem("", "샘플운동6", 60, 600, false)

    )