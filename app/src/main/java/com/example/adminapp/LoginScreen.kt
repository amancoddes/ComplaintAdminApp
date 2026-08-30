package com.example.adminapp




import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
@Composable
fun LoginScreen(
    nav: NavHostController,
    viewModel: LoginScreenViewModel
) {

    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            if (event is LoginEvent.GoHome) {
                nav.navigate("main_Graph") {
                    popUpTo(Screens.LoginScreen.route) { inclusive = true }
                }
            }
        }
    }

    LoginContent(
        email = viewModel.email,
        password = viewModel.password,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onLoginClick = { viewModel.login() },
        onSignupClick = {
            nav.navigate(Screens.SignUpScreen.route)
        },
        isLoading = state is LoginState.Loading,
        error = (state as? LoginState.Error)?.msg
    )
}

@Composable
fun LoginContent(
    email: String,
    password: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onSignupClick: () -> Unit,
    isLoading: Boolean = false,
    error: String? = null
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF5C6BC0),
                        Color(0xFF3949AB),
                        Color(0xFF1A237E)
                    )
                )
            )
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .align(Alignment.Center),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(10.dp)
        ) {

            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text("Welcome Back 👋", fontSize = 22.sp, fontWeight = FontWeight.Bold)

                Spacer(Modifier.height(8.dp))

                Text("Login to continue", color = Color.Gray)

                Spacer(Modifier.height(20.dp))

                if (isLoading) {
                    CircularProgressIndicator()
                } else {

                    if (error != null) {
                        Text(error, color = Color.Red)
                        Spacer(Modifier.height(10.dp))
                    }

                    OutlinedTextField(
                        value = email,
                        onValueChange = onEmailChange,
                        label = { Text("Email") },
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = null)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = onPasswordChange,
                        label = { Text("Password") },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(20.dp))

                    Button(
                        onClick = onLoginClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Login")
                    }

                    Spacer(Modifier.height(12.dp))

                    TextButton(
                        onClick = onSignupClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("New here? Create Account")
                    }
                }
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun LoginPreview() {

//    CityCareTheme {
//        LoginContent(
//            email = "test@gmail.com",
//            password = "wrong",
//            onEmailChange = {},
//            onPasswordChange = {},
//            onLoginClick = {},
//            onSignupClick = {},
//            error = "Invalid credentials"
//        )
//
//    }
}