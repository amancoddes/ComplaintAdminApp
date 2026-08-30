package com.example.adminapp

import android.provider.ContactsContract.Data
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifyCheck(

    view: SignUpScreenViewModel,

    nav: NavHostController

) {

    val state by view.uiState.collectAsState()

    val snackbarHostState =
        remember { SnackbarHostState() }

    //  Snackbar events

    LaunchedEffect(Unit) {

        view.snackbar.collect { message ->

            snackbarHostState.showSnackbar(message)
        }
    }

    //  Navigate after verification success

    LaunchedEffect(state) {

        if (state is DataClassLogin.VerifyComplete) {

            nav.navigate(
                Screens.AdminRequestScreen.route
            ) {

                popUpTo(
                    Screens.IsEmailVerified.route
                ) {

                    inclusive = true
                }
            }
        }
    }

    Scaffold(

        snackbarHost = {

            SnackbarHost(snackbarHostState)

        },

        topBar = {

            TopAppBar(

                title = {

                    Text(

                        text = "Restricted Access",

                        fontSize = 24.sp,

                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }

    ) { innerPadding ->

        VerifyContent(

            isLoading =
            state is DataClassLogin.Loading,

            paddingValues = innerPadding,

            onVerifyClick = {

                view.checkEmailVerified()
            }
        )
    }
}


@Composable
fun VerifyContent(

    isLoading: Boolean,

    onVerifyClick: () -> Unit,

    paddingValues: PaddingValues

) {

    val infiniteTransition =
        rememberInfiniteTransition(label = "")

    val scale by infiniteTransition.animateFloat(

        initialValue = 0.9f,

        targetValue = 1.05f,

        animationSpec = infiniteRepeatable(

            animation = tween(1200),

            repeatMode = RepeatMode.Reverse
        ),

        label = ""
    )

    Box(

        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)

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

            elevation =
            CardDefaults.cardElevation(10.dp)

        ) {

            Column(

                modifier = Modifier.padding(24.dp),

                horizontalAlignment =
                Alignment.CenterHorizontally

            ) {

                //  Animated Email Icon

                Icon(

                    imageVector = Icons.Default.Email,

                    contentDescription = null,

                    tint = Color(0xFF5C6BC0),

                    modifier = Modifier
                        .size(70.dp)
                        .scale(scale)
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                Text(

                    text = "Verify Your Email 📩",

                    fontSize = 22.sp,

                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(

                    text =
                    "We sent a verification link to your email.\n" +
                            "Please open your inbox and verify.",

                    textAlign = TextAlign.Center,

                    color = Color.Gray,

                    fontSize = 15.sp
                )

                Spacer(
                    modifier = Modifier.height(24.dp)
                )

                Button(

                    onClick = onVerifyClick,

                    enabled = !isLoading,

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),

                    shape =
                    RoundedCornerShape(14.dp)

                ) {

                    if (isLoading) {

                        CircularProgressIndicator(

                            modifier = Modifier.size(24.dp),

                            color = Color.White,

                            strokeWidth = 2.dp
                        )

                    } else {

                        Text(

                            text = "I Verified",

                            fontSize = 16.sp
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Text(

                    text =
                    "Didn’t receive email?\nCheck spam folder.",

                    textAlign = TextAlign.Center,

                    fontSize = 12.sp,

                    color = Color.Gray
                )
            }
        }
    }
}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun VerifyCheck(
//    view: SignUpScreenViewModel,
//    nav: NavHostController
//) {
//
//    val state by view.uiState.collectAsState()
//    val snackbarHostState = remember { SnackbarHostState() }
//
//
//    LaunchedEffect(Unit) {
//        view.snackbar.collect { event ->
//            snackbarHostState.showSnackbar(event)
//        }
//    }
//
//    LaunchedEffect(state) {
//        if (state is DataClassLogin.VerifyComplete) {
//           // view.onVerificationSuccess()
//            nav.navigate(Screens.AdminRequestScreen.route) {
//                popUpTo(Screens.IsEmailVerified.route) {
//                    inclusive = true
//                }
//            }
//        }
//    }
//
//    when(state){
//
//        is DataClassLogin.Error ->  TODO()
//
//        is DataClassLogin.Idle -> {
//
//            Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) },topBar = {
//                TopAppBar(title = {
//                    Text(
//                        text = "Restricted Access",
//                        fontSize = 28.sp,
//                        fontWeight = FontWeight.Bold
//                    )
//                })
//            }) {
//                    innerPadding ->
//                VerifyContent(
//                    isLoading = false,
//                    onVerifyClick = { view.checkEmailVerified() },
//                    paddingValues = innerPadding
//                )
//
//            }
//        }
//
//        is DataClassLogin.Loading ->  {
//
//        }
//
//        DataClassLogin.VerifyComplete -> TODO()
//        DataClassLogin.VerifyEmailSent ->  TODO()
//    }
//
//
//
//}
//
//@Composable
//fun VerifyContent(
//    isLoading: Boolean,
//    onVerifyClick: () -> Unit,
//    paddingValues: PaddingValues
//) {
//
//    val infiniteTransition = rememberInfiniteTransition(label = "")
//
//    val scale by infiniteTransition.animateFloat(
//        initialValue = 0.9f,
//        targetValue = 1.05f,
//        animationSpec = infiniteRepeatable(
//            animation = tween(1200),
//            repeatMode = RepeatMode.Reverse
//        ),
//        label = ""
//    )
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize().padding(paddingValues)
//            .background(
//                Brush.verticalGradient(
//                    listOf(
//                        Color(0xFF5C6BC0),
//                        Color(0xFF3949AB),
//                        Color(0xFF1A237E)
//                    )
//                )
//            )
//    ) {
//
//        Card(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(20.dp)
//                .align(Alignment.Center),
//            shape = RoundedCornerShape(24.dp),
//            elevation = CardDefaults.cardElevation(10.dp)
//        ) {
//
//            Column(
//                modifier = Modifier.padding(24.dp),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//
//                //  Icon (animated)
//                Icon(
//                    imageVector = Icons.Default.Email,
//                    contentDescription = null,
//                    tint = Color(0xFF5C6BC0),
//                    modifier = Modifier
//                        .size(60.dp)
//                        .scale(scale)
//                )
//
//                Spacer(Modifier.height(16.dp))
//
//                Text(
//                    text = "Verify Your Email 📩",
//                    fontSize = 20.sp,
//                    fontWeight = FontWeight.Bold
//                )
//
//                Spacer(Modifier.height(8.dp))
//
//                Text(
//                    text = "We have sent a verification link to your email.\nPlease check your inbox and verify.",
//                    textAlign = TextAlign.Center,
//                    color = Color.Gray
//                )
//
//                Spacer(Modifier.height(20.dp))
//
//                Button(
//                    onClick = onVerifyClick,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(50.dp),
//                    shape = RoundedCornerShape(14.dp)
//                ) {
//                    Text("I Verified", fontSize = 16.sp)
//                }
//
//                Spacer(Modifier.height(10.dp))
//
//                Text(
//                    text = "Didn’t receive email? Check spam folder.",
//                    fontSize = 12.sp,
//                    color = Color.Gray
//                )
//
//                if (isLoading) {
//                    Spacer(Modifier.height(16.dp))
//                    CircularProgressIndicator()
//                }
//            }
//        }
//    }
//}
//
//
//
//@Preview(showBackground = true)
//@Composable
//fun VerifyPreview() {
//    VerifyContent(
//        isLoading = false,
//        onVerifyClick = {},
//        paddingValues = PaddingValues()
//    )
//}