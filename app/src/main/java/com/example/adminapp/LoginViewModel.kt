package com.example.adminapp



import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LoginScreenViewModel @Inject constructor(
    private val repo: UserLoginRepository
) : ViewModel() {

    var email by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set
    fun onEmailChange(e: String) { email = e }
    fun onPasswordChange(p: String) { password = p }
    private val _uiState = MutableStateFlow<LoginState>(LoginState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<LoginEvent>()
    val events = _events

    fun login() {
        viewModelScope.launch {
            _uiState.value = LoginState.Loading

            val result = repo.login(email, password)

            result.fold(
                onSuccess = { user ->
                    if (user?.isEmailVerified == true) {
                        _events.emit(LoginEvent.GoHome)
                    } else {
                        _uiState.value =
                            LoginState.Error("Please verify your email")
                    }
                },
                onFailure = { e ->
                    _uiState.value =
                        LoginState.Error(e.message ?: "Unknown error")
                }
            )
        }
    }
}

sealed class LoginState {
    data object Idle : LoginState()
    data object Loading : LoginState()
    data class Error(val msg: String) : LoginState()
}

sealed class LoginEvent {
    data object GoHome : LoginEvent()
}