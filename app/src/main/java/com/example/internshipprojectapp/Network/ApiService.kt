package com.example.internshipprojectapp.Network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST


data class LoginRequest(val username: String, val password: String)
data class LoginResponse(val token: String)
interface ApiService {
    @POST("api/Authentication/Login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>
}
