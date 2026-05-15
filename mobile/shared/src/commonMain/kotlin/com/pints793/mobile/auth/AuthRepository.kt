package com.pints793.mobile.auth

import com.pints793.mobile.api.AuthApi
import com.pints793.mobile.api.UserApi
import com.pints793.mobile.domain.NewUserRequest
import com.pints793.mobile.net.TokenStore
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AuthStatus { Unknown, Authenticated, Unauthenticated }

class AuthRepository(
    private val tokenStore: TokenStore,
    private val authApi: AuthApi,
    private val userApi: UserApi,
) {
    private val _status = MutableStateFlow(if (tokenStore.get() != null) AuthStatus.Unknown else AuthStatus.Unauthenticated)
    val status: StateFlow<AuthStatus> = _status.asStateFlow()

    private val _logoutEvents = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val logoutEvents: SharedFlow<Unit> = _logoutEvents.asSharedFlow()

    /** Verify any cached token at app start. Call from the root composable. */
    suspend fun bootstrap() {
        if (tokenStore.get() == null) {
            _status.value = AuthStatus.Unauthenticated
            return
        }
        val ok = userApi.verifyToken()
        _status.value = if (ok) AuthStatus.Authenticated else AuthStatus.Unauthenticated
        if (!ok) tokenStore.clear()
    }

    suspend fun login(identifier: String, password: String) {
        val res = authApi.login(identifier, password)
        tokenStore.set(res.token)
        _status.value = AuthStatus.Authenticated
    }

    suspend fun register(req: NewUserRequest) {
        val res = authApi.register(req)
        tokenStore.set(res.token)
        _status.value = AuthStatus.Authenticated
    }

    fun logout() {
        tokenStore.clear()
        _status.value = AuthStatus.Unauthenticated
        _logoutEvents.tryEmit(Unit)
    }
}

