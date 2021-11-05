package com.fitsionary.momspt.data.api.response


import android.os.Parcelable
import com.fitsionary.momspt.data.model.MyPageInfoModel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MyPageInfoResponse(
    var babyName: String?,
    val dayAfterBabyDue: Int,
    var nickname: String,
    var thumbnail: String
) : Parcelable

fun MyPageInfoResponse.toModel(): MyPageInfoModel {
    return MyPageInfoModel(
        babyName = babyName ?: "",
        dayAfterBabyDue = dayAfterBabyDue,
        nickname = nickname,
        thumbnail = thumbnail
    )
}