package com.fitsionary.momspt.network

import com.fitsionary.momspt.data.api.request.SignInRequest
import com.fitsionary.momspt.data.api.request.SignUpRequest
import com.fitsionary.momspt.data.api.request.TodayWorkoutListRequest
import com.fitsionary.momspt.data.api.response.*
import io.reactivex.rxjava3.core.Single
import okhttp3.MultipartBody
import retrofit2.http.*

interface Api {
    @POST("/user/login")
    fun signIn(@Body body: SignInRequest): Single<SignInResponse>

    @POST("/user/signup")
    fun signUp(@Body body: SignUpRequest): Single<CommonResponse>

    @GET("/user/nicknameduplicate")
    fun nicknameDuplicateCheck(@Query("nickname") nickname: String): Single<CommonResponse>

    @GET("/user/daycomment")
    fun getTodayComment(): Single<TodayCommentResponse>

    @GET("/workout/workoutlist")
    fun getTodayWorkoutList(): Single<WorkoutListResponse>

    @Multipart
    @POST("/upload")
    fun sendVideo(
        @Part file: MultipartBody.Part
    ): Single<String>

    @GET("workout/keypoints")
    suspend fun getWorkoutPoseLandmark(@Query("workoutcode") workoutCode: String): WorkoutPoseLandmarkResponse
}