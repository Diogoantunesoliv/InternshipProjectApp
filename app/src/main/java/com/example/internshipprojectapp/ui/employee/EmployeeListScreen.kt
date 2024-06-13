package com.example.internshipprojectapp.ui.employee

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.example.internshipprojectapp.data.model.Employee
import com.example.internshipprojectapp.data.repository.EmployeeRepository
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.launch
import android.util.Log

@Composable
fun EmployeeListScreen(
    repository: EmployeeRepository,
    fusedLocationClient: FusedLocationProviderClient,
    onConfirm: (Employee) -> Unit
) {
    var employees by remember { mutableStateOf<List<Employee>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var selectedEmployee by remember { mutableStateOf<Employee?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var currentLatitude by remember { mutableStateOf<Double?>(null) }
    var currentLongitude by remember { mutableStateOf<Double?>(null) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    fun fetchEmployees() {
        scope.launch {
            try {
                repository.getEmployees(
                    onResult = { fetchedEmployees ->
                        employees = fetchedEmployees.take(5)
                        isLoading = false
                    },
                    onError = { error ->
                        errorMessage = error
                        isLoading = false
                    }
                )
            } catch (e: Exception) {
                errorMessage = e.message
                isLoading = false
            }
        }
    }


    fun fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                currentLatitude = it.latitude
                currentLongitude = it.longitude
                Log.d("Location", "Latitude: $currentLatitude, Longitude: $currentLongitude")
            }
        }
    }


    LaunchedEffect(Unit) {
        fetchEmployees()
        fetchLocation()
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {

            Text(
                text = "Lista de Empregados",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (isLoading) {
                Text(text = "Carregando...", color = MaterialTheme.colorScheme.primary)
            } else if (errorMessage != null) {
                Text(text = errorMessage ?: "Erro desconhecido", color = MaterialTheme.colorScheme.error)
            } else {
                LazyColumn {
                    items(employees) { employee ->
                        EmployeeItem(employee, isSelected = employee == selectedEmployee) {
                            selectedEmployee = it
                        }
                    }
                }
            }
        }

        selectedEmployee?.let { employee ->
            currentLatitude?.let { latitude ->
                currentLongitude?.let { longitude ->
                    if (employee.employeeID == "9de1056f-3044-4cb7-8b8e-1493872c69a1" && latitude == 41.2334533 && longitude == -8.6213383) {
                        Button(
                            onClick = {
                                onConfirm(employee)
                                println("Localização igual! Parabéns")
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp)
                        ) {
                            Text(text = "Confirmar Geolocalização")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmployeeItem(employee: Employee, isSelected: Boolean, onClick: (Employee) -> Unit) {
    Column(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .clickable { onClick(employee) }
            .fillMaxWidth()
            .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(text = "ID: ${employee.employeeID}", style = MaterialTheme.typography.bodyLarge)
        Text(text = "Nome: ${employee.name}", style = MaterialTheme.typography.bodyMedium)
    }
}
