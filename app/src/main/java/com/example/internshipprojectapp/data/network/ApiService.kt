package com.example.internshipprojectapp.data.network

import com.example.internshipprojectapp.data.model.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @POST("api/Authentication/Login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @GET("api/Employees/GetAllEmployees")
    fun getAllEmployees(): Call<List<Employee>>
}



