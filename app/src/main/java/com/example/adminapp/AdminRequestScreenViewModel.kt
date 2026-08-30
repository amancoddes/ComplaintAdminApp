package com.example.adminapp

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AdminRequestScreenViewModel @Inject constructor (private val firebase: FirebaseAuth, private val api:RemoteAdminRequest):ViewModel(){

    private val _email = MutableStateFlow<String>("")
    val email = _email.asStateFlow()

    private val _loading = MutableStateFlow<Boolean>(false)
    val loading = _loading.asStateFlow()

    init {
        val user = firebase.currentUser
        _email.value = user?.email?:""
    }


    private val _snackBar = MutableSharedFlow<UiEvent>()
    val snackbar = _snackBar.asSharedFlow()




    fun makeAdminCall() {

        Log.e("TAG", "makeAdminCall:")
        viewModelScope.launch {
            _loading.value = true

            try {


                val user = firebase.currentUser

                if (user == null) {

                    _snackBar.emit(
                        UiEvent.ShowSnackbar(
                            "User not logged in ❌"
                        )
                    )

                    _loading.value = false
                    return@launch
                }

                //  get current token
                val token = user
                    .getIdToken(false)
                    .await()
                    .token

                if (token.isNullOrBlank()) {

                    _snackBar.emit(
                        UiEvent.ShowSnackbar(
                            "Token not found ❌"
                        )
                    )

                    _loading.value = false

                    return@launch
                }

                //  api call
                val response =
                    api.makeAdmin("Bearer $token")

                if (!response.isSuccessful) {
                    Log.e("TAG", " error -> ${response.errorBody()?.string()}:")


                    _snackBar.emit(
                        UiEvent.ShowSnackbar(
                            response.errorBody()
                                ?.string()
                                ?: "Request failed ❌"
                        )
                    )

                    _loading.value = false

                    return@launch
                }

                //  refresh token to get latest claims
                val refreshedToken = user
                    .getIdToken(true)
                    .await()

                val isAdmin =
                    refreshedToken.claims["admin"] == true

                if (isAdmin) {

                    _snackBar.emit(
                        UiEvent.NavigateToHome
                    )

                } else {

                    _snackBar.emit(

                        UiEvent.ShowSnackbar(
                            "Admin claim not updated ❌"
                        )
                    )
                }

            } catch (e: Exception) {

                Log.e("TAG", "Exception run ")
                Log.e("TAG", " ${e.message}")

                _snackBar.emit(

                    UiEvent.ShowSnackbar(
                        e.message
                            ?: "Something went wrong ❌"
                    )
                )

            } finally {

                _loading.value = false
            }
        }
    }
}


sealed class UiEvent {
    data class ShowSnackbar(val message: String) : UiEvent()
    data object NavigateToHome : UiEvent()
}

