package com.pints793.mobile.net

import io.ktor.client.engine.HttpClientEngine

/** Per-platform Ktor engine: OkHttp on Android, Darwin on iOS. */
expect fun httpEngine(): HttpClientEngine

