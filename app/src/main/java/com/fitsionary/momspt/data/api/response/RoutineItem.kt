package com.fitsionary.momspt.data.api.response

data class RoutineItem(
    val thumbnail: String,
    val name: String,
    val time: Int,
    val kcal: Int,
    val isFinish: Boolean
)

fun getSampleRoutineItem() =
    mutableListOf(
        RoutineItem("", "샘플운동1", 10, 100, true),
        RoutineItem("", "샘플운동2", 20, 200, true),
        RoutineItem("", "샘플운동3", 30, 300, false)
    )