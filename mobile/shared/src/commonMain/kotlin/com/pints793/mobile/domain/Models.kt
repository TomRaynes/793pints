package com.pints793.mobile.domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Display label + id pair, used everywhere the backend returns a thin reference. */
@Serializable
data class EntityLabel(val id: String, val name: String)

@Serializable
enum class CaskState(val display: String) {
    @SerialName("delivered")     Delivered("Delivered"),
    @SerialName("racked")        Racked("Racked"),
    @SerialName("settled")       Settled("Settled"),
    @SerialName("vented")        Vented("Vented"),
    @SerialName("needs_tap")     NeedsTap("Needs Tap"),
    @SerialName("tapped")        Tapped("Tapped"),
    @SerialName("ready_to_serve") ReadyToServe("Ready to Serve"),
    @SerialName("pulling")       Pulling("Pulling"),
    @SerialName("tired")         Tired("Tired"),
    ;
    companion object {
        val entriesInOrder: List<CaskState> = listOf(
            Delivered, Racked, Settled, Vented, NeedsTap, Tapped, ReadyToServe, Pulling, Tired
        )

        fun fromDisplay(display: String): CaskState? =
            entriesInOrder.firstOrNull { it.display.equals(display, ignoreCase = true) }
    }
}

@Serializable
data class Cask(
    @SerialName("id")   val caskId: String,
    @SerialName("name") val caskName: String,
    val state: CaskState,
    val stateChangeTimestamp: String, // ISO-8601 from backend; parsed to Instant on demand
    val rackCooldownHours: Double? = null,
    val ventCooldownHours: Double? = null,
    val tapCooldownHours: Double? = null,
    val pullingPeriodHours: Double? = null,
)

@Serializable
data class Cellar(
    val id: String,
    val name: String,
    val organisationId: String,
)

@Serializable
data class CellarConfig(
    val rackCooldownDefault: Double = 0.0,
    val ventCooldownDefault: Double = 0.0,
    val tapCooldownDefault: Double = 0.0,
    val pullingPeriodDefault: Double = 0.0,
)

@Serializable
data class Organisation(
    val id: String,
    val name: String,
)

@Serializable
data class UserProfile(
    val username: String,
    val email: String,
    val name: String? = null,
    val bio: String? = null,
    val profilePicture: String? = null,
)

@Serializable
data class Invitation(
    val id: String,
    val senderUserId: String,
    val recipientUserId: String,
    val organisationId: String,
    val organisationName: String? = null,
    val senderUsername: String? = null,
)

@Serializable
data class PinnedCellarInfo(
    val cellarId: String,
    val cellarName: String,
    val organisationId: String,
    val organisationName: String,
)

@Serializable
data class OrganisationMembersResponse(
    val admins: Map<String, String> = emptyMap(),
    val members: Map<String, String> = emptyMap(),
)

@Serializable
data class AccessLevelResponse(val accessLevel: String)

