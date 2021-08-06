package com.fitsionary.momspt.network

import com.fitsionary.momspt.data.api.request.TodayWorkoutListRequest
import com.fitsionary.momspt.data.api.response.TodayWorkoutListResponse
import com.fitsionary.momspt.data.api.request.PoseRequest
import com.fitsionary.momspt.data.api.response.ScoreResponse
import com.fitsionary.momspt.data.api.response.TodayCommentResponse
import io.reactivex.rxjava3.core.Single
import okhttp3.MultipartBody
import retrofit2.http.*

interface Api {
    @GET("/users/getdaycomment")
    fun getTodayComment(@Query("name") name: String): Single<TodayCommentResponse>

    @POST("/workout/todayworkoutlist")
    fun getTodayWorkoutList(@Body body: TodayWorkoutListRequest): Single<TodayWorkoutListResponse>

    @POST("/pt/input")
    fun sendPose(@Body body: PoseRequest): Single<ScoreResponse>

    @Multipart
    @POST("/upload")
    fun sendVideo(
        @Part file: MultipartBody.Part
    ): Single<String>
}