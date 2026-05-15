package com.pints793.mobile.core

import com.pints793.mobile.config.BuildKonfig

actual object PlatformConfig {
    actual val baseUrl: String = BuildKonfig.ANDROID_BASE_URL
}

