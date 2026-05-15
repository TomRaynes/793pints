package com.pints793.mobile.core

/** Build/runtime configuration. Resolved per platform via [PlatformConfig]. */
expect object PlatformConfig {
    /** Base URL for the backend REST API, e.g. `http://10.0.2.2:8080/api/v1`. */
    val baseUrl: String
}

