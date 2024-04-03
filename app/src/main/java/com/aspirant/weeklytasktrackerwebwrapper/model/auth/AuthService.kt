package com.aspirant.weeklytasktrackerwebwrapper.model.auth

import com.aspirant.weeklytasktrackerwebwrapper.model.entity.response.ApiResponse

interface AuthService {
    companion object {
        fun authHeaderString(token: String) = "Bearer $token"
        fun authHeaderString(authService: AuthService): String? {
            val authToken = authService.getAuthToken() ?: return null
            return authHeaderString(authToken)
        }

        const val TOKEN_KEY = "token"
    }

    suspend fun login(username: String, password: String, onLoginResponse: (ApiResponse<String>?) -> Unit)
    suspend fun register(username: String, password: String, onRegisterResponse: (ApiResponse<Unit>?) -> Unit)
    suspend fun testAuthToken(onTokenValid: () -> Unit, onTokenInvalid: () -> Unit)

    fun getAuthToken(): String?
    fun clearAuthToken(): Unit

    fun logout()
}