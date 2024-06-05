package com.example.internshipprojectapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.internshipprojectapp.data.model.Employee
import com.example.internshipprojectapp.data.repository.EmployeeRepository
import kotlinx.coroutines.launch

class MainViewModel(private val repository: EmployeeRepository) : ViewModel() {

    private val _employees = MutableLiveData<List<Employee>>()
    val employees: LiveData<List<Employee>> = _employees

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun fetchEmployees() {
        viewModelScope.launch {
            repository.getEmployees(
                onResult = { employees ->
                    _employees.postValue(employees.take(3)) // Get the first 5 employees
                },
                onError = { error ->
                    _errorMessage.postValue(error)
                }
            )
        }
    }
}

