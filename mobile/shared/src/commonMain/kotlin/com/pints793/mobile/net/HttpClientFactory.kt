package com.pints793.mobile.net

import com.pints793.mobile.core.PlatformConfig
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLBuilder
import io.ktor.http.contentType
import io.ktor.http.takeFrom
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object HttpClientFactory {
    val json: Json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        explicitNulls = false
        coerceInputValues = true
    }

    fun create(tokenStore: TokenStore, onUnauthorised: () -> Unit): HttpClient =
        HttpClient(httpEngine()) {
            expectSuccess = false

            install(ContentNegotiation) { json(json) }
            install(Logging) { level = LogLevel.INFO }

            defaultRequest {
                contentType(ContentType.Application.Json)
                tokenStore.get()?.let { header(HttpHeaders.Authorization, it) }
                // Resolve every relative request path against the per-platform base URL.
                val base = PlatformConfig.baseUrl.trimEnd('/') + "/"
                val requested = url.encodedPathSegments.filter { it.isNotEmpty() }.joinToString("/")
                url.takeFrom(URLBuilder().takeFrom(base + requested).build())
            }

            HttpResponseValidator {
                validateResponse { response ->
                    val statusCode = response.status.value
                    if (statusCode >= 300) {
                        if (statusCode == HttpStatusCode.Unauthorized.value || statusCode == HttpStatusCode.BadRequest.value) {
                            onUnauthorised()
                        }
                        throw ApiException(statusCode, "HTTP $statusCode for ${response.call.request.url}")
                    }
                }
            }
        }
}


