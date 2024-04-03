package com.aspirant.weeklytasktrackerwebwrapper.model.entity.response

import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

sealed class ApiResponse<out T> {
    data class Success<T>(val success: Boolean, val value: T) : ApiResponse<T>()
    data class Failure(val success: Boolean, val error: String) : ApiResponse<Nothing>()

    companion object {
        inline fun <reified T> typeToken(): Type {
            return object : TypeToken<ApiResponse<T>>() {}.type
        }

        fun <T> success(value: T): ApiResponse<T> = Success(true, value)
        fun failure(error: String): ApiResponse<Nothing> = Failure(false, error)
    }
}

