package com.example.internshipprojectapp.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun BlankScreen(
    navController: NavHostController,
    onLogout: () -> Unit,
    onGetLocationPermission: () -> Unit,
    onGetLocationResult: () -> Unit
) {
    Surface(
        color = Color.White,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Bem-vindo à tela de Localização!",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(16.dp)
            )

            Button(
                onClick = {
                    onGetLocationPermission()
                },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(text = "Solicitar Permissão de Localização")
            }

            /*Button(
                onClick = {
                    onGetLocationResult()
                },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(text = "Obter Localização")
            }*/

            Button(
                onClick = {
                    navController.navigate("employee_list")
                },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(text = "Ir para lista de empregados")
            }

            Button(
                onClick = {
                    onLogout()
                    navController.navigate("LoginScreen") {
                        popUpTo("blankScreen") { inclusive = true }
                    }
                },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(text = "Sair")
            }
        }
    }
}
