package com.example.adminapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminRequestScreen(viewModel:AdminRequestScreenViewModel,navController: NavHostController){

    val snackbarHostState = remember { SnackbarHostState() }
    val isLoading = viewModel.loading.collectAsState().value


    if(isLoading){

        LoadingSection()
    }

    LaunchedEffect(Unit) {

        viewModel.snackbar.collect { event ->

            when (event) {

                is UiEvent.ShowSnackbar -> {

                    snackbarHostState.showSnackbar(event.message)

                }

                UiEvent.NavigateToHome -> {

                    navController.navigate(Screens.mainGraph.route) {

                        popUpTo(Screens.IsEmailVerified.route) { inclusive = true }

                    }

                }

            }

        }

    }
    val email by viewModel.email.collectAsState()
    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) },topBar = {
        TopAppBar(title = {
            Text(
                text = "Restricted Access",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        })
    }) {
        innerPadding ->
        AdminRequestScreen_Content(email = email, onRequestClick = { viewModel.makeAdminCall() }, paddingValues = innerPadding)


    }

}



@Composable
fun AdminRequestScreen_Content(
    email: String,
    onRequestClick: () -> Unit,
    paddingValues: PaddingValues = PaddingValues(0.dp)
) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF6C63FF), Color.White)
                    )
                ).padding(paddingValues)
                .padding(16.dp),Arrangement.Center
        ) {


            Text(
                text = "Special Access Request",
                color = Color.White,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Admin access is restricted to authorized users only.",
                color = Color.Red
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Card
            Card(
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    Text(
                        text = "Your Account",
                        color = Color(0xFF6C63FF),
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {

                        // Avatar
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEDE9FE)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profile",
                                tint = Color(0xFF6C63FF),
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text("Soul User", fontWeight = FontWeight.Bold)
                            Text(email, color = Color.Gray)

                            Spacer(modifier = Modifier.height(4.dp))

                            // Email Verified
                            Text(
                                text = "Email Verified ✓",
                                color = Color(0xFF16A34A),
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    HorizontalDivider()

                    Spacer(modifier = Modifier.height(16.dp))

                    // Info box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF1F5F9), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(
                                "What happens next?",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6C63FF)
                            )
                            Text(
                                "We will verify your account and notify you.",
                                fontSize = 13.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Button
                    Button(
                        onClick = onRequestClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF6C63FF)
                        )
                    ) {
                        Text(
                            "Request Admin Access",
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        "Your request is secure and encrypted 🔒",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Great things happen when you have the right access ✨",
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = Color.Gray
            )
        }

}
@Composable
@Preview(showBackground = true)
fun Content(){
    AdminRequestScreen_Content(email = "soul@gmail.com",{})
}