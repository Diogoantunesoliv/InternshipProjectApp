package com.example.internshipprojectapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.internshipprojectapp.Login.LoginScreen
import com.example.internshipprojectapp.ui.theme.InternshipProjectAppTheme

class MainActivity : ComponentActivity() {
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
                                        Log.d("Sistema", "Credenciais corretas")
                                        //navController.navigate("blankScreen")
                                    } else {
                                        Log.d("Sistema", "Credenciais errados")
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}