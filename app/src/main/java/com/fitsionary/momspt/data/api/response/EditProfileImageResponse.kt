package com.fitsionary.momspt.data.api.response

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EditProfileImageResponse(
    val success: Boolean,
    val profile: String
) : Parcelable