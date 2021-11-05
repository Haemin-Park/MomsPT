package com.fitsionary.momspt.data.api.request

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SignUpRequest(
    var nickname: String,
    var babyDue: String,
    var weightBeforePregnancy: Int?,
    var weightNow: Int?,
    var heightNow: Int?,
    var kakaoId: String,
) : Parcelable