package com.example.internshipprojectapp.data.repository

import com.example.internshipprojectapp.data.model.ApiResponse
import com.example.internshipprojectapp.data.model.Employee
import com.example.internshipprojectapp.data.model.LocationDTO
import com.example.internshipprojectapp.data.network.ApiService
import retrofit2.Response

class EmployeeRepository(private val apiService: ApiService) {

    suspend fun getAllEmployees(onResult: (List<Employee>) -> Unit, onError: (String) -> Unit) {
        try {
            val response = apiService.getAllEmployees()
            if (response.isSuccessful) {
                response.body()?.let { onResult(it) }
            } else {
                onError("Erro ao obter empregados")
            }
        } catch (e: Exception) {
            onError(e.message ?: "Erro desconhecido")
        }
    }

    suspend fun verifyLocation(locationDTO: LocationDTO): Response<ApiResponse> {
        return apiService.verifyLocation(locationDTO)
    }
}
