package com.pints793.mobile.net

import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.KeychainSettings

@OptIn(ExperimentalSettingsImplementation::class)
actual fun createTokenStore(): TokenStore =
    TokenStore(KeychainSettings(service = "com.pints793.mobile"))


