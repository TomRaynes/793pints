package com.pints793.mobile.api

import com.pints793.mobile.domain.Invitation
import com.pints793.mobile.domain.PinnedCellarInfo
import com.pints793.mobile.domain.UpdateProfileRequest
import com.pints793.mobile.domain.UserProfile
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders

class UserApi(private val client: HttpClient) {

    suspend fun verifyToken(): Boolean = runCatching {
        client.get("user/verify_token"); true
    }.getOrElse { false }

    suspend fun getProfile(): UserProfile = client.get("user/profile").body()

    suspend fun getUserProfile(userId: String): UserProfile =
        client.get("user/profile/$userId").body()

    suspend fun updateProfile(req: UpdateProfileRequest): UserProfile =
        client.post("user/profile/update") { setBody(req) }.body()

    suspend fun uploadProfilePicture(bytes: ByteArray, mime: String, filename: String = "avatar"): UserProfile =
        client.post("user/profile/picture") {
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append(
                            key = "file",
                            value = bytes,
                            headers = Headers.build {
                                append(HttpHeaders.ContentType, mime)
                                append(HttpHeaders.ContentDisposition, "filename=\"$filename\"")
                            }
                        )
                    }
                )
            )
        }.body()

    suspend fun getInvitations(): List<Invitation> = client.get("user/invitations").body()

    suspend fun getProfileImage(id: String): String =
        client.get("user/profile_image/$id").body()

    suspend fun getProfileImages(userIds: List<String>): Map<String, String> =
        client.post("user/profile_images") {
            contentType(ContentType.Application.Json); setBody(userIds)
        }.body()

    suspend fun getPinnedCellars(): List<PinnedCellarInfo> =
        client.get("user/pinned_cellars").body()

    suspend fun pinCellar(cellarId: String) {
        client.post("user/cellar/$cellarId/pin")
    }

    suspend fun unpinCellar(cellarId: String) {
        client.post("user/cellar/$cellarId/unpin")
    }
}

private fun io.ktor.client.request.HttpRequestBuilder.contentType(t: ContentType) {
    headers.append(HttpHeaders.ContentType, t.toString())
}

