package com.example.internshipprojectapp.authentication

import android.content.Context

class AuthenticationHandler(private val context: Context) {

    private val biometricHelper = BiometricHelper(context)

    fun authenticate(onSuccess: () -> Unit, onError: (String) -> Unit) {
        biometricHelper.initializeBiometricPrompt(onSuccess, onError)
        biometricHelper.authenticate()
    }

    // Se precisar de métodos adicionais para Face Unlock e PIN, adicione-os aqui
}
