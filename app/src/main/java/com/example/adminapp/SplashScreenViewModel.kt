package com.example.adminapp
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import javax.inject.Inject


@HiltViewModel
class SplashScreenViewModel @Inject constructor(
    private val auth: FirebaseAuth,
) : ViewModel() {

    private val _startDestination = MutableStateFlow<String?>(null)
    val startDestination = _startDestination.asStateFlow()

    init {
        checkUser()
    }

    private fun checkUser() {

        viewModelScope.launch {

            try {

                // refresh firebase user state
                withTimeout(2000L) {

                    auth.currentUser
                        ?.reload()
                        ?.await()
                }

            } catch (e: Exception) {

                Log.e(
                    "SplashViewModel",
                    "User reload failed",
                    e
                )
            }

            val user = auth.currentUser

            //  user not logged in
            if (user == null) {

                _startDestination.value =
                    Screens.LoginScreen.route

                return@launch
            }

            //  email not verified
            if (!user.isEmailVerified) {

                _startDestination.value =
                    Screens.IsEmailVerified.route

                return@launch
            }

            try {

                //  get cached token claims
                val tokenResult =
                    user.getIdToken(false)
                        .await()

                val isAdmin =
                    tokenResult.claims["admin"] == true

                Log.d(
                    "SplashViewModel",
                    "Admin Claim -> $isAdmin"
                )

                //  admin approved
                if (isAdmin) {

                    _startDestination.value =
                        "main_Graph"

                } else {

                    //  email verified but admin not approved
                    _startDestination.value =
                        Screens.AdminRequestScreen.route
                }

            } catch (e: Exception) {

                Log.e(
                    "SplashViewModel",
                    "Token claim check failed",
                    e
                )

                _startDestination.value =
                    Screens.AdminRequestScreen.route
            }
        }
    }
}