package com.pints793.mobile.domain

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(val identifier: String, val password: String)

@Serializable
data class LoginResponse(val token: String)

@Serializable
data class NewUserRequest(
    val username: String,
    val email: String,
    val password: String,
    val confirmPassword: String,
)

@Serializable
data class UpdateProfileRequest(
    val name: String? = null,
    val bio: String? = null,
)

@Serializable
data class NewCellarRequest(val name: String, val organisationId: String)

@Serializable
data class NewOrganisationRequest(val name: String)

@Serializable
data class NewCaskRequest(
    val organisationId: String,
    val cellarId: String,
    val name: String,
    val state: CaskState,
)

@Serializable
data class UpdateCaskRequest(
    val organisationId: String,
    val cellarId: String,
    val caskId: String,
    val name: String? = null,
    val state: CaskState? = null,
    val rackCooldownHours: Double? = null,
    val ventCooldownHours: Double? = null,
    val tapCooldownHours: Double? = null,
    val pullingPeriodHours: Double? = null,
)

@Serializable
data class RemoveCaskRequest(
    val organisationId: String,
    val cellarId: String,
    val caskId: String,
)

@Serializable
data class CellarConfigField(val value: Double, val applyToAll: Boolean = false)

@Serializable
data class UpdateCellarConfigRequest(
    val rackCooldownDefault: CellarConfigField? = null,
    val ventCooldownDefault: CellarConfigField? = null,
    val tapCooldownDefault: CellarConfigField? = null,
    val pullingPeriodDefault: CellarConfigField? = null,
)

@Serializable
data class InviteRequest(val identifier: String)

@Serializable
data class AcceptInviteRequest(val invitationId: String)

