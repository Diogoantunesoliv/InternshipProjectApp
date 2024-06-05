package com.example.internshipprojectapp.data.repository

import com.example.internshipprojectapp.data.model.Employee
import com.example.internshipprojectapp.data.model.EmployeesResponse
import com.example.internshipprojectapp.data.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EmployeeRepository(private val apiService: ApiService) {
    fun getEmployees(onResult: (List<Employee>) -> Unit, onError: (String) -> Unit) {
        apiService.getAllEmployees().enqueue(object : Callback<List<Employee>> {
            override fun onResponse(call: Call<List<Employee>>, response: Response<List<Employee>>) {
                if (response.isSuccessful) {
                    val employees = response.body() ?: emptyList()
                    onResult(employees)
                } else {
                    onError("Falha ao obter empregados: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<Employee>>, t: Throwable) {
                onError("Erro na chamada da API: ${t.message}")
            }
        })
    }
}
