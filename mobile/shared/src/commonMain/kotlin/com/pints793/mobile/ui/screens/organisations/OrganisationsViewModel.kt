package com.pints793.mobile.ui.screens.organisations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pints793.mobile.api.OrganisationApi
import com.pints793.mobile.di.ServiceLocator
import com.pints793.mobile.domain.EntityLabel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class OrganisationsUiState(
    val organisations: List<EntityLabel> = emptyList(),
    val loading: Boolean = true,
    val creating: Boolean = false,
    val error: String? = null,
)

class OrganisationsViewModel(
    private val orgApi: OrganisationApi = ServiceLocator.organisationApi,
) : ViewModel() {

    private val _state = MutableStateFlow(OrganisationsUiState())
    val state: StateFlow<OrganisationsUiState> = _state.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null)
            try {
                val list = orgApi.getAll().sortedBy { it.name.lowercase() }
                _state.value = _state.value.copy(organisations = list, loading = false)
            } catch (t: Throwable) {
                _state.value = _state.value.copy(loading = false, error = t.message)
            }
        }
    }

    /** @return true on success so the caller can dismiss its dialog. */
    fun create(name: String, onDone: (Boolean) -> Unit) {
        val trimmed = name.trim()
        if (trimmed.isEmpty() || _state.value.creating) {
            onDone(false); return
        }
        viewModelScope.launch {
            _state.value = _state.value.copy(creating = true, error = null)
            val ok = runCatching { orgApi.create(trimmed) }
                .onFailure { _state.value = _state.value.copy(error = it.message) }
                .isSuccess
            _state.value = _state.value.copy(creating = false)
            if (ok) load()
            onDone(ok)
        }
    }
}

