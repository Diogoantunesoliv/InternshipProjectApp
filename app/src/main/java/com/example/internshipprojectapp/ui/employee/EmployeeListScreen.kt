package com.example.internshipprojectapp.ui.employee

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import com.example.internshipprojectapp.authentication.DeviceAuthenticator
import com.example.internshipprojectapp.data.model.Employee
import com.example.internshipprojectapp.data.model.LocationDTO
import com.example.internshipprojectapp.data.repository.EmployeeRepository
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun EmployeeListScreen(
    navController: NavController,
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
    var successMessage by remember { mutableStateOf<String?>(null) }
    var isAuthenticating by remember { mutableStateOf(false) }
    var isAuthenticated by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val authenticator = DeviceAuthenticator(context)

    fun fetchEmployees() {
        scope.launch {
            try {
                repository.getAllEmployees(
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
        scope.launch {
            try {
                val location = fusedLocationClient.lastLocation.await()
                location?.let {
                    currentLatitude = it.latitude
                    currentLongitude = it.longitude
                    Log.d("Location", "Latitude: ${it.latitude}, Longitude: ${it.longitude}")
                }
            } catch (e: Exception) {
                errorMessage = e.message
            }
        }
    }

    fun verifyLocation(employee: Employee) {
        scope.launch {
            try {
                val latitude = currentLatitude
                val longitude = currentLongitude
                if (latitude != null && longitude != null) {
                    val locationDTO = LocationDTO(latitude, longitude)
                    val response = repository.verifyLocation(locationDTO)
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody?.value == "Location is correct") {
                            successMessage = "Parabéns, localização correta!"
                            errorMessage = null
                            onConfirm(employee)
                        } else {
                            successMessage = null
                            errorMessage = "Local errado. Desloca-se ao local correto para validação."
                            println("Localização incorreta")
                        }
                    } else {
                        successMessage = null
                        errorMessage = "Erro na verificação de localização: ${response.errorBody()?.string()}"
                        println("Erro na verificação de localização: ${response.errorBody()?.string()}")
                    }
                } else {
                    successMessage = null
                    errorMessage = "Localização não disponível"
                    println("Localização não disponível")
                }
            } catch (e: Exception) {
                successMessage = null
                errorMessage = "Erro na verificação de localização: ${e.message}"
            }
        }
    }

    fun authenticateAndVerifyLocation(employee: Employee) {
        if (isAuthenticating || isAuthenticated) return
        isAuthenticating = true
        authenticator.authenticate({
            isAuthenticating = false
            isAuthenticated = true
            verifyLocation(employee)
        }, {
            isAuthenticating = false
            successMessage = null
            errorMessage = "Autenticação falhou: $it"
            Log.e("Auth", "Autenticação falhou: $it")
        })
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
                modifier = Modifier.padding(top = 70.dp, bottom = 16.dp)
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
                            isAuthenticated = false
                        }
                    }
                }
            }

            successMessage?.let {
                Text(text = it, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 16.dp))
            }
        }

        selectedEmployee?.let { employee ->
            Column {
                Button(
                    onClick = {
                        authenticateAndVerifyLocation(employee)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text(text = "Confirmar Geolocalização")
                }
                Button(
                    onClick = {
                        navController.navigateUp()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text(text = "Voltar")
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
        Text(
            text = "Nome: ${employee.name}",
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text = "ID: ${employee.employeeID}",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
