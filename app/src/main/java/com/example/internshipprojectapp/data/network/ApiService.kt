package com.example.internshipprojectapp.data.network

import com.example.internshipprojectapp.data.model.ApiResponse
import com.example.internshipprojectapp.data.model.Employee
import com.example.internshipprojectapp.data.model.LocationDTO
import com.example.internshipprojectapp.data.model.LoginRequest
import com.example.internshipprojectapp.data.model.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @POST("api/Authentication/Login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("api/Employees/GetAllEmployees")
    suspend fun getAllEmployees(): Response<List<Employee>>

    @POST("api/Employees/VerifyLocation")
    suspend fun verifyLocation(@Body locationDTO: LocationDTO): Response<ApiResponse>

}
