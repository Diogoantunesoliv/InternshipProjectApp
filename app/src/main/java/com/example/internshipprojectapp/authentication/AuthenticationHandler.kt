package com.example.internshipprojectapp.authentication

import android.content.Context
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AuthenticationHandler(private val context: Context) {

    private val biometricHelper = BiometricHelper(context)

    fun authenticate(onSuccess: () -> Unit, onError: (String) -> Unit) {
        when {
            biometricHelper.isFaceIdAvailable() -> {
                biometricHelper.initializeBiometricPrompt(onSuccess, onError)
                biometricHelper.authenticate()
            }
            biometricHelper.isFaceUnlockAvailable() -> {
                biometricHelper.initializeBiometricPrompt(onSuccess, onError)
                biometricHelper.authenticate()
            }
            else -> {
                showPinDialog(onSuccess, onError)
            }
        }
    }

    private fun showPinDialog(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val pinEditText = EditText(context).apply {
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD
        }
        MaterialAlertDialogBuilder(context)
            .setTitle("Insira o PIN")
            .setView(pinEditText)
            .setPositiveButton("Confirmar") { _, _ ->
                val enteredPin = pinEditText.text.toString()
                if (validatePin(enteredPin)) {
                    onSuccess()
                } else {
                    onError("PIN incorreto")
                }
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
                onError("Autenticação cancelada")
            }
            .show()
    }

    private fun validatePin(enteredPin: String): Boolean {
        // Substitua isso com a lógica real de validação do PIN
        return enteredPin == "1234"
    }
}
