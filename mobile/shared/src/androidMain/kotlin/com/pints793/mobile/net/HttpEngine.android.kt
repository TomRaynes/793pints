package com.pints793.mobile.net

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp

actual fun httpEngine(): HttpClientEngine = OkHttp.create()

