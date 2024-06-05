package com.example.internshipprojectapp.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.internshipprojectapp.viewmodel.MainViewModel

@Composable
fun BlankScreen(
    navController: NavHostController,
    viewModel: MainViewModel = viewModel(),
    onLogout: () -> Unit,
    onGetLocationPermission: () -> Unit,
    onGetLocationResult: () -> Unit,
    onGetEmployees: () -> Unit
) {
    val employees by viewModel.employees.observeAsState(emptyList())
    val errorMessage by viewModel.errorMessage.observeAsState()
    var showDialog by remember { mutableStateOf(false) }

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

            Button(
                onClick = {
                    onGetLocationResult()
                },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(text = "Obter Localização")
            }

            Button(
                onClick = {
                    onGetEmployees()
                    showDialog = true
                },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(text = "Obter Empregados")
            }

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text(text = "Lista de Empregados") },
                    text = {
                        Column {
                            employees.forEach { employee ->
                                Text(text = "${employee.name}")
                            }
                            errorMessage?.let {
                                Text(text = it, color = Color.Red)
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = { showDialog = false }
                        ) {
                            Text("Fechar")
                        }
                    }
                )
            }

            Button(
                onClick = {
                    onLogout()
                    navController.navigate("loginScreen") {
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
