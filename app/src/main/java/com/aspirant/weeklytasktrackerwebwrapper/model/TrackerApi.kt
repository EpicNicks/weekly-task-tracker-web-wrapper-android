package com.aspirant.weeklytasktrackerwebwrapper.model


import com.aspirant.weeklytasktrackerwebwrapper.model.entity.request.LoginRequest
import com.aspirant.weeklytasktrackerwebwrapper.model.entity.response.ApiResponse
import com.aspirant.weeklytasktrackerwebwrapper.model.entity.response.UserResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface TrackerApi {

    // account queries
    @GET("account/userInfo")
    fun getUserInfo(@Header("Authorization") authString: String): Call<ApiResponse<UserResponse>>

    @POST("account/login")
    fun login(@Body requestBody: LoginRequest): Call<ApiResponse<String>>

    @POST("account/register")
    fun register(@Body requestBody: LoginRequest): Call<ApiResponse<Unit>>
}

