package com.fitsionary.momspt.network

import com.fitsionary.momspt.data.api.request.*
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
    fun getTodayInfo(): Single<TodayInfoResponse>

    @GET("/workout/workoutlist")
    fun getTodayWorkoutList(): Single<WorkoutListResponse>

    @POST("/workout/workoutlist")
    fun getStepWorkoutList(@Body body: StepWorkoutRequest): Single<WorkoutListResponse>

    @POST("/workout/workoutresult")
    fun sendWorkoutResult(@Body body: WorkoutResultRequest): Single<WorkoutResultResponse>

    @GET("/workout/weeklyworkoutstatistics")
    fun getWeeklyAchieved(): Single<WeeklyAchievedResponse>

    @GET("/daily/day/todayanalysis")
    fun getTodayStatistics(): Single<TodayStatisticsResponse>

    @POST("/daily/day/weight")
    fun editWeight(
        @Body body: EditWeightRequest
    ): Single<CommonResponse>

    @GET("/daily/day/weeklystatistics")
    fun getWeeklyStatistics(): Single<WeeklyStatisticsResponse>

    @GET("/daily/month/monthlystatistics")
    fun getMonthlyStatistics(
        @Query("year") year: Int,
        @Query("month") month: Int
    ): Single<MonthlyStatisticsResponse>

    @POST("/daily/month/detailstatistics")
    fun getDayStatisticsDetail(@Body body: DayStatisticsDetailRequest): Single<DayStatisticsDetailResponse>

    @Multipart
    @POST("/upload")
    fun sendVideo(
        @Part file: MultipartBody.Part
    ): Single<String>

    @GET("/workout/keypoints")
    suspend fun getWorkoutPoseLandmark(@Query("workoutcode") workoutCode: String): WorkoutPoseLandmarkResponse
}