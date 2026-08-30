package com.example.adminapp

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController


// LISTEN SNACKBAR EVENTS
//    LaunchedEffect(Unit) {
//        viewModel.snackbar.collect { msg ->
//            snackbarHost.showSnackbar(msg)
//        }
//    }



@Composable
fun SignUpScreen(
    nav: NavHostController,
    viewModel: SignUpScreenViewModel
) {
    var emailError by remember { mutableStateOf(false) }

    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state) {
        if (state is DataClassLogin.VerifyEmailSent) {
            nav.navigate(Screens.IsEmailVerified.route) {
                popUpTo(Screens.SignUpScreen.route) {
                    inclusive = true
                }
            }
        }
    }

    SignupContent(
        email = viewModel.email.value,
        password = viewModel.password.value,//viewModel::loginInpu
        onEmailChange = { email ->
            viewModel.loginInput(input = email)
            emailError=!Patterns.EMAIL_ADDRESS.matcher(email).matches()
        },
        onPasswordChange = viewModel::passwordInput,
        onSignupClick = { viewModel.signUp() },
        onLoginClick = {
            nav.navigate(Screens.LoginScreen.route) {
                popUpTo(Screens.SignUpScreen.route) {
                    inclusive = true
                }
            }
        },
        isLoading = state is DataClassLogin.Loading,
        error = (state as? DataClassLogin.Error)?.e,
        emailError = emailError
    )
}




@Composable
fun SignupContent(
    email: String,
    password: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSignupClick: () -> Unit,
    onLoginClick: () -> Unit,
    isLoading: Boolean = false,
    error: String? = null,
    emailError: Boolean
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

                Text(
                    text = "Create Account 🚀",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Join us and get started",
                    color = Color.Gray
                )

                Spacer(Modifier.height(20.dp))

                if (isLoading) {
                    CircularProgressIndicator()
                } else {

                    if (error != null) {
                        Text(error, color = Color.Red)
                        Spacer(Modifier.height(10.dp))
                    }

                    //  Email
                    OutlinedTextField(
                        value = email,
                        onValueChange ={ email ->
                            onEmailChange(email)
                        },
                        supportingText = {
                            if(emailError){
                                Text("invalid Email", style = MaterialTheme.typography.labelSmall, color = Color.Red)
                            }
                        },
                        label = { Text("Email") },
                        leadingIcon = {
                            Icon(Icons.Default.Email, null)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(Modifier.height(12.dp))

                    //  Password
                    OutlinedTextField(
                        value = password,
                        onValueChange = onPasswordChange,
                        label = { Text("Password") },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, null)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    PasswordValidationUI(password)
                    Spacer(Modifier.height(20.dp))

                    //  Primary Button
                    Button(
                        onClick = onSignupClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Sign Up", fontSize = 16.sp)
                    }

                    Spacer(Modifier.height(12.dp))

                    //  Secondary Button
                    TextButton(
                        onClick = onLoginClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Already have an account? Login")
                    }
                }
            }
        }
    }
}









@Preview(showBackground = true)
@Composable
fun SignupPreview() {

    SignupContent(
        email = "user@gmail.com",
        password = "1Ra#",
        onEmailChange = {},
        onPasswordChange = {},
        onSignupClick = {},
        onLoginClick = {},
        isLoading = false,
        error = null,
        emailError = true
    )
}

@Preview
@Composable
fun SignupLoadingPreview() {

    SignupContent(
        email = "",
        password = "",
        onEmailChange = {},
        onPasswordChange = {},
        onSignupClick = {},
        onLoginClick = {},
        isLoading = true,
        emailError = false
    )
}

@Preview
@Composable
fun SignupErrorPreview() {

    SignupContent(
        email = "user@gmail.com",
        password = "123",
        onEmailChange = {},
        onPasswordChange = {},
        onSignupClick = {},
        onLoginClick = {},
        error = "Email already exists",
        emailError = true
    )
}
fun passValidation(input: String): List<Pair<String, Boolean>> {

    val rules = listOf<Pair<String, (String) -> Boolean>>(
        "At least one UpperCase" to { it.any { c -> c.isUpperCase() } },
        "At least one LowerCase" to { it.any { c -> c.isLowerCase() } },
        "At least one Special Character" to { it.any { c -> !c.isLetterOrDigit() } },
        "At least one Number" to { it.any { c -> c.isDigit() } },
        "Minimum 8 characters" to { it.length >= 8 }
    )

    return rules.map { (msg, check) ->
        msg to check(input)
    }
}
@Composable
fun PasswordValidationUI(password: String) {

    val rules = remember(password) {
        passValidation(password)
    }

    Column(modifier = Modifier.fillMaxWidth()) {

        rules.forEach { (rule, isValid) ->

            Row(verticalAlignment = Alignment.CenterVertically) {

                Icon(
                    imageVector = if (isValid)
                        Icons.Default.Check
                    else
                        Icons.Default.Close,
                    contentDescription = null,
                    tint = if (isValid) Color(0xFF4CAF50) else Color.Red,
                    modifier = Modifier.size(16.dp)
                )

                Spacer(Modifier.width(6.dp))

                Text(
                    text = rule,
                    fontSize = 12.sp,
                    color = if (isValid) Color(0xFF4CAF50) else Color.Red
                )
            }

            Spacer(Modifier.height(4.dp))
        }
    }
}