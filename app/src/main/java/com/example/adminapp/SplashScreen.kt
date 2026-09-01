package com.example.adminapp

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay


@Composable
fun SplashScreen(nav: NavHostController, viewModel: SplashScreenViewModel) {

    val navigateScreen by viewModel.startDestination.collectAsState()
    LaunchedEffect(navigateScreen) {
        delay(3000)
        if (navigateScreen != null) {
            nav.navigate(navigateScreen!!) {
                popUpTo(Screens.SplashScreen.route) { inclusive = true }
            }

        }
    }
    SplashContent()

}

@Composable
fun SplashContent() {

    val infiniteTransition = rememberInfiniteTransition(label = "")

    // scale animation
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )

    //  alpha animation
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        Color(0xFF1A237E), // deep blue
                        Color(0xFF3949AB), // mid blue
                        Color(0xFF5C6BC0)  // light blue
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {

        //  glowing circle effect
        Box(
            modifier = Modifier
                .size(220.dp)
                .scale(scale)
                .background(
                    color = Color.White.copy(alpha = 0.1f),
                    shape = CircleShape
                )
        )

        //  logo
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
                .border(3.dp, Color.White, CircleShape)
                .alpha(alpha)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SplashPreview() {


}

// test 1