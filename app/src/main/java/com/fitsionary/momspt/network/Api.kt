package com.fitsionary.momspt.network

import com.fitsionary.momspt.data.api.request.PoseRequest
import com.fitsionary.momspt.data.api.response.ScoreResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.Body
import retrofit2.http.POST

interface Api {
    @POST("/pt/input")
    fun sendPose(@Body body: PoseRequest): Single<ScoreResponse>
}