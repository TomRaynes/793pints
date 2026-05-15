package com.pints793.mobile.net

import com.russhwolf.settings.Settings

/**
 * Persistent token storage for the auth bearer token. Concrete [Settings] is supplied per
 * platform — EncryptedSharedPreferences on Android, KeychainSettings on iOS.
 */
class TokenStore internal constructor(private val settings: Settings) {
    fun get(): String? = settings.getStringOrNull(KEY)
    fun set(token: String) { settings.putString(KEY, token) }
    fun clear() { settings.remove(KEY) }

    companion object { private const val KEY = "auth_token" }
}

/** Per-platform factory wired up by [com.pints793.mobile.di.ServiceLocator]. */
expect fun createTokenStore(): TokenStore


