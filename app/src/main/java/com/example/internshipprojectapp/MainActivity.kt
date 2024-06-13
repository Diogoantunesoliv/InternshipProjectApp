package com.example.internshipprojectapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.internshipprojectapp.ui.login.LoginScreen
import com.example.internshipprojectapp.data.repository.EmployeeRepository
import com.example.internshipprojectapp.ui.main.BlankScreen
import com.example.internshipprojectapp.ui.theme.InternshipProjectAppTheme
import com.example.internshipprojectapp.data.network.RetrofitClient
import com.example.internshipprojectapp.ui.employee.EmployeeListScreen
import com.example.internshipprojectapp.authentication.AuthenticationHandler
import com.google.android.gms.location.*

class MainActivity : ComponentActivity() {
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001
    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }

    private val apiService by lazy {
        RetrofitClient.api
    }
    private val employeeRepository by lazy {
        EmployeeRepository(apiService)
    }

    private lateinit var authenticationHandler: AuthenticationHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inicializar o handler de autenticação
        authenticationHandler = AuthenticationHandler(this)

        setContent {
            InternshipProjectAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(navController, startDestination = "LoginScreen") {
                        composable("LoginScreen") {
                            LoginScreen(
                                navController = navController,
                                onLoginResult = { isLoginValid ->
                                    if (isLoginValid) {
                                        Log.d("Sistema-entrar", "Credenciais corretas")
                                        navController.navigate("blankScreen")
                                    } else {
                                        Toast.makeText(this@MainActivity, "Email/Password errados. Tente novamente!", Toast.LENGTH_SHORT).show()
                                        Log.d("Sistema-entrar", "Credenciais erradas")
                                    }
                                }
                            )
                        }
                        composable("blankScreen") {
                            BlankScreen(
                                navController = navController,
                                onLogout = {
                                    Log.d("Sistema-entrar", "Logout efetuado com sucesso")
                                },
                                onGetLocationPermission = {
                                    requestLocationPermission()
                                },
                                onGetLocationResult = {
                                    getLocation()
                                }
                            )
                        }
                        composable("employee_list") {
                            EmployeeListScreen(
                                repository = employeeRepository,
                                fusedLocationClient = fusedLocationClient,
                                onConfirm = { employee ->
                                    // Solicitar autenticação antes de confirmar a geolocalização
                                    authenticationHandler.authenticate(
                                        onSuccess = {
                                            Log.d("Geolocalização", "Autenticação bem-sucedida para: ${employee.name}")
                                            println("Localização igual! Parabéns")
                                        },
                                        onError = { error ->
                                            Log.e("Geolocalização", "Erro de autenticação: $error")
                                            Toast.makeText(this@MainActivity, error, Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission()
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                Log.d("Sistema-Localização", "Latitude: ${it.latitude}, Longitude: ${it.longitude}")
            } ?: run {
                Log.d("Sistema-Localização", "Localização não disponível")
            }
        }

        // Forçar atualização da localização
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 10000
        ).setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(5000)
            .setMaxUpdateDelayMillis(10000)
            .build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let {
                    Log.d("Sistema-Localização-Atualizada", "Latitude: ${it.latitude}, Longitude: ${it.longitude}")
                }
                fusedLocationClient.removeLocationUpdates(this)
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            } else {
                Toast.makeText(this, "Permissão de localização negada", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
