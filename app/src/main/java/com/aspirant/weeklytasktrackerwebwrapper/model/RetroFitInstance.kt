package com.aspirant.weeklytasktrackerwebwrapper.model

import com.aspirant.weeklytasktrackerwebwrapper.model.entity.response.ApiResponseAdapter
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL_PI = "https://freetime.49385219.xyz/api/"

    val api: TrackerApi by lazy {
        val gson = GsonBuilder()
            .registerTypeAdapterFactory(ApiResponseAdapter.FACTORY)
            .create()
        Retrofit.Builder()
            .baseUrl(BASE_URL_PI)
            .addConverterFactory(
                GsonConverterFactory
                    .create(gson)
            )
            .build()
            .create(TrackerApi::class.java)
    }
}