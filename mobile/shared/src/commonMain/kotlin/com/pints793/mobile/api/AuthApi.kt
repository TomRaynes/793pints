package com.pints793.mobile.api

import com.pints793.mobile.domain.LoginRequest
import com.pints793.mobile.domain.LoginResponse
import com.pints793.mobile.domain.NewUserRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class AuthApi(private val client: HttpClient) {

    suspend fun login(identifier: String, password: String): LoginResponse =
        client.post("user/login") { setBody(LoginRequest(identifier, password)) }.body()

    suspend fun register(req: NewUserRequest): LoginResponse =
        client.post("user/new") { setBody(req) }.body()
}

