package com.pints793.mobile.api

import com.pints793.mobile.domain.AcceptInviteRequest
import com.pints793.mobile.domain.AccessLevelResponse
import com.pints793.mobile.domain.EntityLabel
import com.pints793.mobile.domain.InviteRequest
import com.pints793.mobile.domain.NewOrganisationRequest
import com.pints793.mobile.domain.OrganisationMembersResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class OrganisationApi(private val client: HttpClient) {

    suspend fun getAll(): List<EntityLabel> =
        client.get("organisation/get_all").body()

    suspend fun create(name: String): EntityLabel =
        client.post("organisation/new") { setBody(NewOrganisationRequest(name)) }.body()

    suspend fun invite(organisationId: String, identifier: String) {
        client.post("organisation/$organisationId/invite") { setBody(InviteRequest(identifier)) }
    }

    suspend fun accessLevel(organisationId: String): AccessLevelResponse =
        client.get("organisation/$organisationId/access_level").body()

    suspend fun members(organisationId: String): OrganisationMembersResponse =
        client.get("organisation/$organisationId/members").body()

    suspend fun acceptInvite(invitationId: String) {
        client.post("organisation/accept_invite") { setBody(AcceptInviteRequest(invitationId)) }
    }
}

