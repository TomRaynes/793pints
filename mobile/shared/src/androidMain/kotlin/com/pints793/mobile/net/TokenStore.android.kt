package com.pints793.mobile.net

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.pints793.mobile.di.ServiceLocator
import com.russhwolf.settings.SharedPreferencesSettings

private const val PREFS_NAME = "pints_secure_prefs"

actual fun createTokenStore(): TokenStore {
    val ctx: Context = (ServiceLocator.androidAppContext as? Context)
        ?: error("ServiceLocator.initAndroid(applicationContext) was not called from MainActivity")
    val masterKey = MasterKey.Builder(ctx)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
    val prefs = EncryptedSharedPreferences.create(
        ctx,
        PREFS_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )
    return TokenStore(SharedPreferencesSettings(prefs))
}


