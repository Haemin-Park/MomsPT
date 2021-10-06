package com.fitsionary.momspt.network

import com.fitsionary.momspt.util.BASE_URL
import com.fitsionary.momspt.util.CurrentUser
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkService {
    private val okHttpClient = OkHttpClient.Builder()
        .addNetworkInterceptor {
            val request = it.request()
                .newBuilder()
                .addHeader("x-access-token", CurrentUser.token)
                .build()
            it.proceed(request)
        }
        .connectTimeout(500, TimeUnit.SECONDS)
        .readTimeout(500, TimeUnit.SECONDS)
        .writeTimeout(500, TimeUnit.SECONDS)
        .build()

    private val gson = GsonBuilder().setLenient().create()

    private val retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .baseUrl(BASE_URL)
        .build()

    val api: Api = retrofit.create(Api::class.java)
}