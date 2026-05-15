package com.pints793.mobile.api

import com.pints793.mobile.domain.Cask
import com.pints793.mobile.domain.CaskState
import com.pints793.mobile.domain.NewCaskRequest
import com.pints793.mobile.domain.RemoveCaskRequest
import com.pints793.mobile.domain.UpdateCaskRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class CaskApi(private val client: HttpClient) {

    suspend fun getAll(organisationId: String, cellarId: String): List<Cask> =
        client.get("cask/get_all/$organisationId/$cellarId").body()

    suspend fun create(organisationId: String, cellarId: String, name: String, state: CaskState): Cask =
        client.post("cask/new") {
            setBody(NewCaskRequest(organisationId, cellarId, name, state))
        }.body()

    suspend fun update(req: UpdateCaskRequest): Cask =
        client.post("cask/update") { setBody(req) }.body()

    suspend fun remove(organisationId: String, cellarId: String, caskId: String) {
        client.post("cask/remove") {
            setBody(RemoveCaskRequest(organisationId, cellarId, caskId))
        }
    }
}

