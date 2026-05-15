package com.pints793.mobile.di

import com.pints793.mobile.api.AuthApi
import com.pints793.mobile.api.CaskApi
import com.pints793.mobile.api.CellarApi
import com.pints793.mobile.api.OrganisationApi
import com.pints793.mobile.api.UserApi
import com.pints793.mobile.auth.AuthRepository
import com.pints793.mobile.net.HttpClientFactory
import com.pints793.mobile.net.TokenStore
import com.pints793.mobile.net.createTokenStore
import io.ktor.client.HttpClient

/**
 * Tiny hand-rolled DI singleton. Avoids pulling in Koin for a small project.
 * Call [initAndroid] from MainActivity (provides the Application context for EncryptedSharedPreferences).
 */
object ServiceLocator {

    /** Set on Android only — iOS does not need a host context for Keychain. */
    var androidAppContext: Any? = null
        private set

    fun initAndroid(context: Any) {
        androidAppContext = context
    }

    val tokenStore: TokenStore by lazy { createTokenStore() }

    private var _httpClient: HttpClient? = null
    val httpClient: HttpClient
        get() = _httpClient ?: HttpClientFactory.create(
            tokenStore = tokenStore,
            onUnauthorised = { authRepository.logout() }
        ).also { _httpClient = it }

    val authApi: AuthApi by lazy { AuthApi(httpClient) }
    val userApi: UserApi by lazy { UserApi(httpClient) }
    val organisationApi: OrganisationApi by lazy { OrganisationApi(httpClient) }
    val cellarApi: CellarApi by lazy { CellarApi(httpClient) }
    val caskApi: CaskApi by lazy { CaskApi(httpClient) }

    val authRepository: AuthRepository by lazy { AuthRepository(tokenStore, authApi, userApi) }

    /** In-memory cache for navigation hand-off — equivalent to React Router's location.state. */
    val navCache: NavCache by lazy { NavCache() }
}



