package com.pints793.mobile.net

/** Thrown for any non-2xx response. The 401/400 cases also drive auto-logout. */
class ApiException(
    val status: Int,
    message: String,
) : RuntimeException(message) {
    val isUnauthorised: Boolean get() = status == 401 || status == 400
}

