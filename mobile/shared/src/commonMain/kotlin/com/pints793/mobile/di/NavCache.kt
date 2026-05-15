package com.pints793.mobile.di

import com.pints793.mobile.domain.EntityLabel
import com.pints793.mobile.domain.OrganisationMembersResponse

/**
 * Per-navigation cached blobs that are too large to round-trip via nav arguments.
 * Mirrors the React Router `location.state` cache used by OrganisationPage / CellarPage.
 */
class NavCache {
    data class OrgCache(
        val cellars: List<EntityLabel>,
        val accessLevel: String?,
        val members: OrganisationMembersResponse?,
        val memberImages: Map<String, String>,
    )

    private val byOrg: MutableMap<String, OrgCache> = mutableMapOf()

    fun put(orgId: String, cache: OrgCache) { byOrg[orgId] = cache }
    fun get(orgId: String): OrgCache? = byOrg[orgId]
    fun clear() { byOrg.clear() }
}

