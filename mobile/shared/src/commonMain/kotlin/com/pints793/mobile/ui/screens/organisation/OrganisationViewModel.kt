package com.pints793.mobile.ui.screens.organisation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pints793.mobile.api.CellarApi
import com.pints793.mobile.api.OrganisationApi
import com.pints793.mobile.api.UserApi
import com.pints793.mobile.di.NavCache
import com.pints793.mobile.di.ServiceLocator
import com.pints793.mobile.domain.EntityLabel
import com.pints793.mobile.domain.OrganisationMembersResponse
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class OrganisationUiState(
    val cellars: List<EntityLabel> = emptyList(),
    val accessLevel: String? = null,
    val members: OrganisationMembersResponse? = null,
    val memberImages: Map<String, String> = emptyMap(),
    val loading: Boolean = true,
    val creatingCellar: Boolean = false,
    val inviting: Boolean = false,
    val inviteSuccess: String? = null,
    val error: String? = null,
)

class OrganisationViewModel(
    private val orgId: String,
    private val orgApi: OrganisationApi = ServiceLocator.organisationApi,
    private val cellarApi: CellarApi = ServiceLocator.cellarApi,
    private val userApi: UserApi = ServiceLocator.userApi,
    private val navCache: NavCache = ServiceLocator.navCache,
) : ViewModel() {

    private val _state = MutableStateFlow(OrganisationUiState())
    val state: StateFlow<OrganisationUiState> = _state.asStateFlow()

    init {
        // Same gotcha as the React fix: trust the cache only when accessLevel is present.
        val cached = navCache.get(orgId)
        if (cached?.accessLevel != null) {
            _state.value = OrganisationUiState(
                cellars = cached.cellars,
                accessLevel = cached.accessLevel,
                members = cached.members,
                memberImages = cached.memberImages,
                loading = false,
            )
        } else {
            load()
        }
    }

    fun load() {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null)
            try {
                val cellarsDef  = async { cellarApi.getAll(orgId) }
                val accessDef   = async { orgApi.accessLevel(orgId).accessLevel }
                val membersDef  = async { orgApi.members(orgId) }

                val cellars = cellarsDef.await().sortedBy { it.name.lowercase() }
                val access  = accessDef.await()
                val members = membersDef.await()

                val ids = (members.admins.keys + members.members.keys).toList()
                val images = if (ids.isNotEmpty()) {
                    runCatching { userApi.getProfileImages(ids) }.getOrDefault(emptyMap())
                } else emptyMap()

                _state.value = _state.value.copy(
                    cellars = cellars,
                    accessLevel = access,
                    members = members,
                    memberImages = images,
                    loading = false,
                )
                writeCache()
            } catch (t: Throwable) {
                _state.value = _state.value.copy(loading = false, error = t.message)
            }
        }
    }

    fun createCellar(name: String, onDone: (Boolean) -> Unit) {
        val trimmed = name.trim()
        if (trimmed.isEmpty() || _state.value.creatingCellar) { onDone(false); return }
        viewModelScope.launch {
            _state.value = _state.value.copy(creatingCellar = true, error = null)
            val ok = runCatching { cellarApi.create(trimmed, orgId) }
                .onFailure { _state.value = _state.value.copy(error = it.message) }
                .isSuccess
            _state.value = _state.value.copy(creatingCellar = false)
            if (ok) load()
            onDone(ok)
        }
    }

    fun invite(identifier: String, onDone: (Boolean) -> Unit) {
        val trimmed = identifier.trim()
        if (trimmed.isEmpty() || _state.value.inviting) { onDone(false); return }
        viewModelScope.launch {
            _state.value = _state.value.copy(inviting = true, error = null)
            val ok = runCatching { orgApi.invite(orgId, trimmed) }
                .onFailure { _state.value = _state.value.copy(error = it.message) }
                .isSuccess
            _state.value = _state.value.copy(
                inviting = false,
                inviteSuccess = if (ok) "Invitation sent to $trimmed" else null,
            )
            onDone(ok)
        }
    }

    fun clearInviteFlash() { _state.value = _state.value.copy(inviteSuccess = null) }

    /** Persist current snapshot before navigating to a child cellar. */
    fun writeCache() {
        val s = _state.value
        navCache.put(
            orgId,
            NavCache.OrgCache(
                cellars = s.cellars,
                accessLevel = s.accessLevel,
                members = s.members,
                memberImages = s.memberImages,
            )
        )
    }
}

