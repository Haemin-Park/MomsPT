package com.fitsionary.momspt.data.api.request

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EditProfileRequest(
    val nickname: String,
    val babyName: String
) : Parcelable