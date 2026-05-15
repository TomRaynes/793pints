package com.pints793.mobile.ui.screens.invitations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pints793.mobile.api.OrganisationApi
import com.pints793.mobile.api.UserApi
import com.pints793.mobile.di.ServiceLocator
import com.pints793.mobile.domain.Invitation
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class InvitationsUiState(
    val invitations: List<Invitation> = emptyList(),
    val senderImages: Map<String, String?> = emptyMap(),
    val loading: Boolean = true,
    val error: String? = null,
)

class InvitationsViewModel(
    private val userApi: UserApi = ServiceLocator.userApi,
    private val orgApi: OrganisationApi = ServiceLocator.organisationApi,
) : ViewModel() {

    private val _state = MutableStateFlow(InvitationsUiState())
    val state: StateFlow<InvitationsUiState> = _state.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null)
            try {
                val invs = userApi.getInvitations()
                _state.value = _state.value.copy(invitations = invs, loading = false)
                // Fetch sender images in parallel keyed by invitation id.
                val pairs = invs.map { inv ->
                    async {
                        inv.id to runCatching { userApi.getProfileImage(inv.id) }.getOrNull()
                    }
                }.awaitAll()
                _state.value = _state.value.copy(senderImages = pairs.toMap())
            } catch (t: Throwable) {
                _state.value = _state.value.copy(loading = false, error = t.message)
            }
        }
    }

    fun accept(invitation: Invitation) {
        viewModelScope.launch {
            runCatching { orgApi.acceptInvite(invitation.id) }
                .onSuccess {
                    _state.value = _state.value.copy(
                        invitations = _state.value.invitations.filterNot { it.id == invitation.id }
                    )
                }
                .onFailure { _state.value = _state.value.copy(error = it.message) }
        }
    }
}

