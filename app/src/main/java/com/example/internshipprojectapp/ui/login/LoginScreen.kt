package com.example.internshipprojectapp.ui.login

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.internshipprojectapp.R
import com.example.internshipprojectapp.data.model.LoginRequest
import com.example.internshipprojectapp.data.network.RetrofitClient
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavHostController, onLoginResult: (Boolean) -> Unit) {
    val emailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }
    val image = painterResource(id = R.drawable.nclock)
    var passwordVisibility by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = image,
            contentDescription = "Descrição da imagem",
            modifier = Modifier.width(250.dp).height(250.dp).padding(vertical = 45.dp)
        )
        Text(
            text = "Login",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 45.dp)
        )

        OutlinedTextField(
            value = emailState.value,
            onValueChange = { emailState.value = it },
            label = { Text(text = "Login") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            singleLine = true
        )

        OutlinedTextField(
            value = passwordState.value,
            onValueChange = { passwordState.value = it },
            label = { Text(text = "Password") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = {
                    passwordVisibility = !passwordVisibility
                }) {
                    Icon(
                        imageVector = if (passwordVisibility) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (passwordVisibility) "Show password" else "Hide password"
                    )
                }
            },
            visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation()
        )

        Button(
            onClick = {
                val email = emailState.value
                val password = passwordState.value
                coroutineScope.launch {
                    performLogin(email, password, onLoginResult)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text(text = "Entrar")
        }
    }
}

suspend fun performLogin(username: String, password: String, onLoginResult: (Boolean) -> Unit) {
    val apiService = RetrofitClient.api
    val loginRequest = LoginRequest(username, password)

    try {
        val response = apiService.login(loginRequest)
        if (response.isSuccessful && response.body() != null) {
            val token = response.body()?.token
            if (!token.isNullOrEmpty()) {
                Log.d("Login", "Login successful, token: $token")
                // Armazene o token
                onLoginResult(true)
            } else {
                Log.d("Login", "Login failed: token is null or empty")
                onLoginResult(false)
            }
        } else {
            Log.d("Login", "Login failed: response not successful or body is null")
            onLoginResult(false)
        }
    } catch (e: Exception) {
        Log.e("Login", "Error: ${e.message}")
        onLoginResult(false)
    }
}
