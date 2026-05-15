package com.pints793.mobile.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pints793.mobile.api.UserApi
import com.pints793.mobile.di.ServiceLocator
import com.pints793.mobile.domain.PinnedCellarInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DashboardUiState(
    val pinnedCellars: List<PinnedCellarInfo> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
)

class DashboardViewModel(
    private val userApi: UserApi = ServiceLocator.userApi,
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardUiState())
    val state: StateFlow<DashboardUiState> = _state.asStateFlow()

    init { refresh() }

    fun refresh() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            try {
                val pinned = userApi.getPinnedCellars()
                _state.value = DashboardUiState(pinnedCellars = pinned, isLoading = false)
            } catch (t: Throwable) {
                _state.value = _state.value.copy(isLoading = false, errorMessage = t.message)
            }
        }
    }
}

