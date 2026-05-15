package com.pints793.mobile.ui.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe nav routes mirroring the React Router routes.
 * Cached blobs (members, member images, cellars list, access level) live in
 * [com.pints793.mobile.di.NavCache] keyed by orgId, NOT in route arguments.
 */
sealed interface Route {
    @Serializable data object Login : Route
    @Serializable data object Dashboard : Route
    @Serializable data object Organisations : Route
    @Serializable data class Organisation(val orgId: String, val orgName: String) : Route
    @Serializable data class Cellar(
        val orgId: String,
        val orgName: String,
        val cellarId: String,
        val cellarName: String,
    ) : Route
    @Serializable data object Profile : Route
    @Serializable data object EditProfile : Route
    @Serializable data class MemberProfile(val userId: String) : Route
    @Serializable data object Invitations : Route
}

