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
    val isFinish: Boolean,
    val rank: String
) : Parcelable

fun getTestData() =
    listOf(
        WorkoutModel(
            12,
            "통증완화",
            "코어운동",
            "힙 브릿지 운동",
            100,
            "https://www.dementianews.co.kr/news/photo/201902/1501_1270_5524.jpg",
            "통증 완화",
            true,
            "A+"
        ),
        WorkoutModel(
            12,
            "통증완화",
            "코어운동",
            "힙 브릿지 운동",
            100,
            "https://www.dementianews.co.kr/news/photo/201902/1501_1270_5524.jpg",
            "통증 완화",
            true,
            "B"
        ),
        WorkoutModel(
            12,
            "통증완화",
            "코어운동",
            "힙 브릿지 운동",
            100,
            "https://www.dementianews.co.kr/news/photo/201902/1501_1270_5524.jpg",
            "통증 완화",
            true,
            "C"
        )
    )