package com.example.internshipprojectapp

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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.internshipprojectapp.ui.login.LoginScreen
import com.example.internshipprojectapp.data.repository.EmployeeRepository
import com.example.internshipprojectapp.ui.main.BlankScreen
import com.example.internshipprojectapp.ui.theme.InternshipProjectAppTheme
import android.Manifest
import androidx.activity.viewModels
import com.example.internshipprojectapp.data.network.ApiService
import com.example.internshipprojectapp.data.network.RetrofitClient
import com.example.internshipprojectapp.viewmodel.MainViewModel
import com.example.internshipprojectapp.viewmodel.MainViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

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

    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(employeeRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
                                viewModel = viewModel,
                                onLogout = {
                                    Log.d("Sistema-entrar", "Logout efetuado com sucesso")
                                },
                                onGetLocationPermission = {
                                    requestLocationPermission()
                                },
                                onGetLocationResult = {
                                    getLocation()
                                },
                                onGetEmployees = {
                                    viewModel.fetchEmployees()
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
    }
}
