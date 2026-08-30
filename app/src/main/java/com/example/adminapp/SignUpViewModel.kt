package com.example.adminapp





import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject



@HiltViewModel
class SignUpScreenViewModel @Inject constructor(
    private val repo: UserLoginRepository,
    private val auth:FirebaseAuth
) : ViewModel() {

    private val _email = mutableStateOf("")
    val email: State<String> = _email

    fun loginInput(input: String) { _email.value = input }

    private val _password = mutableStateOf("")
    val password: State<String> = _password

    fun passwordInput(input: String) { _password.value = input }

    private val _uiState = MutableStateFlow<DataClassLogin>(DataClassLogin.Idle)
    val uiState = _uiState.asStateFlow()

    // SnackBar EVENTS
    private val _snackbar = MutableSharedFlow<String>()
    val snackbar = _snackbar.asSharedFlow()

    private suspend fun showSnack(msg: String) {
        _snackbar.emit(msg)
    }


    fun onVerificationSuccess() {
        val uid = auth.currentUser?.uid ?: return
        Log.e("listener","listener start from the email verification 🌞")
    }

    fun signUp() {
        viewModelScope.launch {
            _uiState.value = DataClassLogin.Loading

            val result = repo.singnUpRepo(email.value, password.value)

            result.fold(
                onFailure = { e ->
                    _uiState.value = DataClassLogin.Error(e.message ?: "unknown")
                    showSnack(e.message ?: "Signup failed")
                },
                onSuccess = {
                    _uiState.value = DataClassLogin.VerifyEmailSent
                }
            )
        }
    }


    fun checkEmailVerified() {
        _uiState.value = DataClassLogin.Loading
        viewModelScope.launch {
            val verified = repo.isEmailVerified()

            if (verified) {
                _uiState.value = DataClassLogin.VerifyComplete
            } else {
                _uiState.value = DataClassLogin.Error("Email not verified yet")
                showSnack("Email not verified yet")
            }
        }
    }
}


sealed class DataClassLogin{

    data object Idle:DataClassLogin()
    data object Loading:DataClassLogin()
    data object VerifyEmailSent :DataClassLogin()
    data object VerifyComplete:DataClassLogin()
    data class Error(val e:String):DataClassLogin()

}