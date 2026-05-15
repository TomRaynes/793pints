package com.pints793.mobile.api

import com.pints793.mobile.domain.CellarConfig
import com.pints793.mobile.domain.EntityLabel
import com.pints793.mobile.domain.GetAllCellarsRequest
import com.pints793.mobile.domain.NewCellarRequest
import com.pints793.mobile.domain.UpdateCellarConfigRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class CellarApi(private val client: HttpClient) {

    suspend fun getAll(organisationId: String): List<EntityLabel> =
        client.post("cellar/get/all") { setBody(GetAllCellarsRequest(organisationId)) }.body()

    suspend fun create(name: String, organisationId: String) {
        // Backend returns 200 with empty body — don't try to deserialize.
        client.post("cellar/new") { setBody(NewCellarRequest(name, organisationId)) }
    }

    suspend fun getConfig(cellarId: String): CellarConfig =
        client.get("cellar/$cellarId/config").body()

    suspend fun updateConfig(cellarId: String, req: UpdateCellarConfigRequest) {
        client.post("cellar/$cellarId/update_config") { setBody(req) }
    }
}



