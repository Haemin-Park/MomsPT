package com.fitsionary.momspt.network

import com.fitsionary.momspt.data.api.request.PoseRequest
import com.fitsionary.momspt.data.api.response.ScoreResponse
import io.reactivex.rxjava3.core.Single
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface Api {
    @POST("/pt/input")
    fun sendPose(@Body body: PoseRequest): Single<ScoreResponse>

    @Multipart
    @POST("/upload")
    fun sendVideo(
        @Part file: MultipartBody.Part
    ): Single<String>
}