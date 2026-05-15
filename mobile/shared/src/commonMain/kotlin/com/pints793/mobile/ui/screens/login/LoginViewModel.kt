package com.pints793.mobile.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pints793.mobile.auth.AuthRepository
import com.pints793.mobile.di.ServiceLocator
import com.pints793.mobile.domain.NewUserRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val mode: Mode = Mode.Login,
    val identifier: String = "",
    val email: String = "",
    val username: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
) {
    enum class Mode { Login, Register }
}

class LoginViewModel(
    private val auth: AuthRepository = ServiceLocator.authRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    fun setMode(mode: LoginUiState.Mode) = _state.update { it.copy(mode = mode, errorMessage = null) }
    fun setIdentifier(v: String) = _state.update { it.copy(identifier = v) }
    fun setEmail(v: String)      = _state.update { it.copy(email = v) }
    fun setUsername(v: String)   = _state.update { it.copy(username = v) }
    fun setPassword(v: String)   = _state.update { it.copy(password = v) }
    fun setConfirm(v: String)    = _state.update { it.copy(confirmPassword = v) }

    fun submit(onSuccess: () -> Unit) {
        val s = _state.value
        if (s.isSubmitting) return
        _state.update { it.copy(isSubmitting = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                if (s.mode == LoginUiState.Mode.Login) {
                    auth.login(s.identifier.trim(), s.password)
                } else {
                    auth.register(
                        NewUserRequest(
                            username = s.username.trim(),
                            email = s.email.trim(),
                            password = s.password,
                            confirmPassword = s.confirmPassword,
                        )
                    )
                }
                onSuccess()
            } catch (t: Throwable) {
                _state.update { it.copy(errorMessage = t.message ?: "Something went wrong", isSubmitting = false) }
                return@launch
            }
            _state.update { it.copy(isSubmitting = false) }
        }
    }
}

