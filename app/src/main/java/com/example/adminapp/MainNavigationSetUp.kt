package com.example.adminapp

import android.window.SplashScreen
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation

sealed class Screens(val route:String){
    data object SplashScreen:Screens(route= "splashScreen")
    data object LoginScreen:Screens(route = "loginScreen")
    data object IsEmailVerified:Screens(route = "emailVerified")
    data object HomeScreen:Screens(route = "homeScreen")
    data object SignUpScreen:Screens(route = "signUpScreen")
    data object AdminRequestScreen:Screens(route = "adminRequestScreen")
    data object mainGraph:Screens(route = "main_Graph")
    data object ComplaintDetailScreen:Screens(route = "complaint_detail/{id}")
}


@Composable
fun MainNavigationSetUP(navHostController: NavHostController ){


    NavHost(navController = navHostController, startDestination = Screens.SplashScreen.route) {

        composable(route = Screens.SplashScreen.route){

            val viewSplash: SplashScreenViewModel = hiltViewModel()
            SplashScreen(navHostController, viewSplash)
        }

        composable(route = Screens.LoginScreen.route){
            val viewLogin:LoginScreenViewModel= hiltViewModel()
            LoginScreen(navHostController,viewLogin)
        }

        composable(route = Screens.IsEmailVerified.route ){
            val viewEmail:SignUpScreenViewModel= hiltViewModel()
            VerifyCheck(viewEmail,navHostController)
        }


        composable(route = Screens.SignUpScreen.route){
            val viewSignUp:SignUpScreenViewModel = hiltViewModel()
            SignUpScreen(navHostController,viewSignUp)
        }

        composable(route=Screens.AdminRequestScreen.route){
            val viewAdminRequest:AdminRequestScreenViewModel= hiltViewModel()
            AdminRequestScreen(viewAdminRequest,navHostController)
        }



        navigation(route = Screens.mainGraph.route, startDestination = Screens.HomeScreen.route){

            composable(route = Screens.HomeScreen.route){
                val homeView:HomeScreenViewModel= hiltViewModel()

                HomeScreen(homeView,navHostController)
            }


            composable(
                route = "complaint_detail/{id}"
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id")!!
                val view:ComplaintDetailViewModel= hiltViewModel()
                ComplaintDetailScreen(id = id,view,navHostController)
            }
        }
    }



}