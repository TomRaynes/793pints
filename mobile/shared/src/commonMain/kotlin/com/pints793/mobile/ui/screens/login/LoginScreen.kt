package com.pints793.mobile.ui.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pints793.mobile.ui.theme.PintsColors

@Composable
fun LoginScreen(onLoggedIn: () -> Unit) {
    val vm = remember { LoginViewModel() }
    val state by vm.state.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF1F1A17), Color(0xFF2A221C), Color(0xFF3A312B))
                )
            ),
        contentAlignment = Alignment.Center,
    ) {
        Card(
            modifier = Modifier
                .padding(24.dp)
                .widthIn(max = 420.dp),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = PintsColors.Surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 24.dp),
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 28.dp, vertical = 36.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    "793 ",
                    color = PintsColors.Primary,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineLarge,
                )
                Text(
                    "Pints",
                    color = PintsColors.Accent,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineLarge,
                )
                Spacer(Modifier.height(4.dp))
                Text("Track every cask, sip by sip.", color = PintsColors.TextMuted, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(28.dp))

                if (state.mode == LoginUiState.Mode.Login) {
                    OutlinedTextField(
                        value = state.identifier,
                        onValueChange = vm::setIdentifier,
                        label = { Text("Username or email") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                } else {
                    OutlinedTextField(
                        value = state.username,
                        onValueChange = vm::setUsername,
                        label = { Text("Username") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = state.email,
                        onValueChange = vm::setEmail,
                        label = { Text("Email") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = state.password,
                    onValueChange = vm::setPassword,
                    label = { Text("Password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                )

                if (state.mode == LoginUiState.Mode.Register) {
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = state.confirmPassword,
                        onValueChange = vm::setConfirm,
                        label = { Text("Confirm password") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                state.errorMessage?.let { msg ->
                    Spacer(Modifier.height(12.dp))
                    Text(msg, color = PintsColors.Danger, style = MaterialTheme.typography.bodySmall)
                }

                Spacer(Modifier.height(20.dp))
                Button(
                    onClick = { vm.submit(onLoggedIn) },
                    enabled = !state.isSubmitting,
                    colors = ButtonDefaults.buttonColors(containerColor = PintsColors.Accent),
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                ) {
                    Text(if (state.isSubmitting) "…" else if (state.mode == LoginUiState.Mode.Login) "Sign in" else "Create account")
                }

                Spacer(Modifier.height(8.dp))
                TextButton(onClick = {
                    vm.setMode(if (state.mode == LoginUiState.Mode.Login) LoginUiState.Mode.Register else LoginUiState.Mode.Login)
                }) {
                    Text(
                        if (state.mode == LoginUiState.Mode.Login) "Need an account? Register"
                        else "Already have one? Sign in",
                        color = PintsColors.Accent
                    )
                }
            }
        }
    }
}



