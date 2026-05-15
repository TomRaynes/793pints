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
data class NewCellarRequest(val cellarName: String, val organisationId: String)

@Serializable
data class NewOrganisationRequest(val name: String)

@Serializable
data class NewCaskRequest(
    val organisationId: String,
    val cellarId: String,
    val caskName: String,
    val state: CaskState,
)

@Serializable
data class UpdateCaskRequest(
    val organisationId: String,
    val cellarId: String,
    val caskId: String,
    val caskName: String? = null,
    val state: CaskState? = null,
    // Backend declares these as String (raw form-input). Empty string = leave unchanged.
    val rackCooldownHours: String? = null,
    val ventCooldownHours: String? = null,
    val tapCooldownHours: String? = null,
    val pullingPeriodHours: String? = null,
)

@Serializable
data class RemoveCaskRequest(
    val organisationId: String,
    val cellarId: String,
    val caskId: String,
)

@Serializable
data class GetAllCellarsRequest(val organisationId: String)

@Serializable
data class GetAllCasksRequest(val organisationId: String, val cellarId: String)

@Serializable
data class GetAccessLevelRequest(val organisationId: String)

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
data class InviteRequest(val organisationId: String, val recipientIdentifier: String)

@Serializable
data class AcceptInviteRequest(val invitationId: String)

