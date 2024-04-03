package com.aspirant.weeklytasktrackerwebwrapper.model.auth

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.aspirant.weeklytasktrackerwebwrapper.model.RetrofitInstance
import com.aspirant.weeklytasktrackerwebwrapper.model.entity.request.LoginRequest
import com.aspirant.weeklytasktrackerwebwrapper.model.entity.response.ApiResponse
import com.aspirant.weeklytasktrackerwebwrapper.model.entity.response.UserResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SharedPreferencesAuthService(context: Context) : AuthService {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("auth", Context.MODE_PRIVATE)


    override suspend fun login(
        username: String,
        password: String,
        onLoginResponse: (ApiResponse<String>?) -> Unit
    ) {
        val call = RetrofitInstance.api.login(LoginRequest(username, password))
        call.enqueue(object : Callback<ApiResponse<String>> {
            override fun onFailure(call: Call<ApiResponse<String>>, t: Throwable) {
                Log.e("loginResponse", "failure ${t.message}")
            }

            override fun onResponse(call: Call<ApiResponse<String>>, response: Response<ApiResponse<String>>) {
                Log.i("loginResponse", "${response.headers()}, ${response.body()}")
                Log.i("loginResponse", "success: ${response.isSuccessful}, response code: ${response.code()}")
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null) {
                        when (loginResponse) {
                            is ApiResponse.Success -> {
                                with(sharedPreferences.edit()) {
                                    putString(AuthService.TOKEN_KEY, loginResponse.value)
                                    apply()
                                }
                            }

                            is ApiResponse.Failure -> {}
                        }
                    }
                    onLoginResponse(loginResponse)
                } else {
                    val loginResponse = response.errorBody()
                    if (loginResponse != null) {
                        Log.e("loginResponse", "login error body: $loginResponse")
                    }
                    onLoginResponse(ApiResponse.failure(loginResponse.toString()))
                }
            }
        })
    }

    override suspend fun register(
        username: String,
        password: String,
        onRegisterResponse: (ApiResponse<Unit>?) -> Unit
    ) {
        val call = RetrofitInstance.api.register(LoginRequest(username, password))
        call.enqueue(object : Callback<ApiResponse<Unit>> {
            override fun onFailure(call: Call<ApiResponse<Unit>>, t: Throwable) {
                Log.e("registerResponse", "failure ${t.message}")
            }

            override fun onResponse(call: Call<ApiResponse<Unit>>, response: Response<ApiResponse<Unit>>) {
                Log.i("registerResponse", "${response.headers()}, ${response.body()}")
                Log.i("registerResponse", "success: ${response.isSuccessful}, response code: ${response.code()}")
                if (response.isSuccessful) {
                    val registerResponse = response.body()
                    onRegisterResponse(registerResponse)
                } else {
                    val registerResponse = response.errorBody()
                    if (registerResponse != null) {
                        Log.e("registerResponse", "register error body: $registerResponse")
                    }
                    onRegisterResponse(ApiResponse.failure(registerResponse.toString()))
                }
            }
        })
    }

    override suspend fun testAuthToken(onTokenValid: () -> Unit, onTokenInvalid: () -> Unit) {
        val authToken = getAuthToken()
        if (authToken == null) {
            onTokenInvalid()
            return
        }
        val logTag = "test auth token on user info"
        val call = RetrofitInstance.api.getUserInfo(AuthService.authHeaderString(authToken))
        call.enqueue(object : Callback<ApiResponse<UserResponse>> {
            override fun onResponse(
                call: Call<ApiResponse<UserResponse>>,
                response: Response<ApiResponse<UserResponse>>
            ) {
                Log.i(logTag, "${response.headers()}, ${response.code()}, ${response.body()}")
                Log.i(logTag, "success: ${response.isSuccessful}, response code: ${response.code()}")
                if (response.isSuccessful) {
                    onTokenValid()
                } else {
                    onTokenInvalid()
                }
            }

            override fun onFailure(call: Call<ApiResponse<UserResponse>>, throwable: Throwable) {
                Log.e(logTag, "onFailure called ${throwable.message}")
                onTokenInvalid()
            }

        })
    }

    override fun getAuthToken(): String? {
        return sharedPreferences.getString(AuthService.TOKEN_KEY, null)
    }

    override fun clearAuthToken() {
        with(sharedPreferences.edit()) {
            putString(AuthService.TOKEN_KEY, null)
            apply()
        }
    }

    override fun logout() {
        with(sharedPreferences.edit()) {
            putString(AuthService.TOKEN_KEY, null)
            apply()
        }
    }
}