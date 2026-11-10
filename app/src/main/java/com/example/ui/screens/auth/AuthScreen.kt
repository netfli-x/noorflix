package com.example.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.NoorFlixLogo
import com.example.ui.theme.*
import com.example.ui.viewmodel.NoorFlixViewModel

@Composable
fun AuthScreen(
    viewModel: NoorFlixViewModel,
    onAuthSuccess: () -> Unit
) {
    var isRegisterMode by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("amira@noorflix.com") }
    var password by remember { mutableStateOf("noorflix2024") }
    var username by remember { mutableStateOf("Amira Al-Mansoor") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = NoorPrimaryBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_form_container"),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                NoorFlixLogo(size = 48)

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = if (isRegisterMode) "Create an Account" else "Welcome Back",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 24.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (isRegisterMode) "Join millions watching short cinematic dramas" else "Sign in to access your watch history & coins",
                    color = NoorTextSecondary,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Username field (Register mode)
                if (isRegisterMode) {
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Your Name / Username") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = NoorGold) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = NoorElevatedSurface,
                            unfocusedContainerColor = NoorCardBackground,
                            focusedBorderColor = NoorGold,
                            unfocusedBorderColor = NoorBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("auth_username_input")
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Email field
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = NoorGold) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = NoorElevatedSurface,
                        unfocusedContainerColor = NoorCardBackground,
                        focusedBorderColor = NoorGold,
                        unfocusedBorderColor = NoorBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("auth_email_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Password field
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = NoorGold) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null,
                                tint = NoorTextMuted
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = NoorElevatedSurface,
                        unfocusedContainerColor = NoorCardBackground,
                        focusedBorderColor = NoorGold,
                        unfocusedBorderColor = NoorBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("auth_password_input")
                )

                if (!isRegisterMode) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showForgotPasswordDialog = true }) {
                            Text("Forgot password?", color = NoorGoldLight, fontSize = 12.sp)
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Sign in / Register Button
                Button(
                    onClick = {
                        if (email.isNotBlank() && password.isNotBlank()) {
                            viewModel.showMessage("Welcome to NoorFlix!")
                            onAuthSuccess()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NoorGold, contentColor = NoorPrimaryBackground),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("auth_primary_button")
                ) {
                    Text(
                        text = if (isRegisterMode) "Create Account" else "Sign In",
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Guest Mode Button
                OutlinedButton(
                    onClick = {
                        viewModel.showMessage("Browsing as Guest")
                        onAuthSuccess()
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NoorBorder),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("auth_guest_button")
                ) {
                    Text("Continue as Guest", fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Toggle Mode
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (isRegisterMode) "Already have an account?" else "Don't have an account?",
                        color = NoorTextSecondary,
                        fontSize = 13.sp
                    )
                    TextButton(onClick = { isRegisterMode = !isRegisterMode }) {
                        Text(
                            text = if (isRegisterMode) "Sign In" else "Sign Up",
                            color = NoorGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        if (showForgotPasswordDialog) {
            var resetEmail by remember { mutableStateOf(email) }
            AlertDialog(
                onDismissRequest = { showForgotPasswordDialog = false },
                title = { Text("Reset Password", color = Color.White, fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text("Enter your email address and we'll send a password recovery code.", color = NoorTextSecondary, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = resetEmail,
                            onValueChange = { resetEmail = it },
                            label = { Text("Email") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                containerColor = NoorElevatedSurface,
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.showMessage("Password reset instructions sent to $resetEmail")
                            showForgotPasswordDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NoorGold, contentColor = NoorPrimaryBackground)
                    ) {
                        Text("Send Reset Link")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showForgotPasswordDialog = false }) {
                        Text("Cancel", color = NoorTextMuted)
                    }
                }
            )
        }
    }
}
