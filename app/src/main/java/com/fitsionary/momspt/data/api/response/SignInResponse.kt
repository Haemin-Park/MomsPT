package com.fitsionary.momspt.data.api.response


import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@Parcelize
data class SignInResponse(
    val success: Boolean,
    val user: User
) : Parcelable