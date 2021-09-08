package com.fitsionary.momspt.network

import com.fitsionary.momspt.data.api.request.NicknameDuplicateCheckRequest
import com.fitsionary.momspt.data.api.request.SignUpRequest
import com.fitsionary.momspt.data.api.request.TodayWorkoutListRequest
import com.fitsionary.momspt.data.api.response.CommonResponse
import com.fitsionary.momspt.data.api.response.TodayCommentResponse
import com.fitsionary.momspt.data.api.response.TodayWorkoutListResponse
import com.fitsionary.momspt.data.api.response.WorkoutPoseLandmarkResponse
import io.reactivex.rxjava3.core.Single
import okhttp3.MultipartBody
import retrofit2.http.*

interface Api {
    @POST("/user/signup")
    fun signUp(@Body body: SignUpRequest): Single<CommonResponse>

    @POST("/user/nicknameduplicatecheck")
    fun nicknameDuplicateCheck(@Body body: NicknameDuplicateCheckRequest): Single<CommonResponse>

    @GET("/users/getdaycomment")
    fun getTodayComment(@Query("name") name: String): Single<TodayCommentResponse>

    @POST("/workout/todayworkoutlist")
    fun getTodayWorkoutList(@Body body: TodayWorkoutListRequest): Single<TodayWorkoutListResponse>

    @Multipart
    @POST("/upload")
    fun sendVideo(
        @Part file: MultipartBody.Part
    ): Single<String>

    @GET("")
    fun getWorkoutPoseLandmark(): Single<WorkoutPoseLandmarkResponse>
}