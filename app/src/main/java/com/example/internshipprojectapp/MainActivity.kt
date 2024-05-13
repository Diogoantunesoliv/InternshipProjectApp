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
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.internshipprojectapp.Login.LoginScreen
import com.example.internshipprojectapp.Navegacao.BlankScreen
import com.example.internshipprojectapp.ui.theme.InternshipProjectAppTheme
import android.Manifest

class MainActivity : ComponentActivity() {
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001
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
                                onLogout = {
                                    Log.d("Sistema-entrar", "Logout efetuado com sucesso")
                                },
                                onGetLocationPermission = {
                                    requestLocationPermission()
                                },
                                onGetLocationResult = {
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(
                this,
                "Permissão de localização já concedida.",
                Toast.LENGTH_SHORT
            ).show()
            Log.d("Permissao", "Permissão de localização já concedida.")
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            Log.d("Permissao", "Solicitando permissão de localização...")
        }
    }
}
