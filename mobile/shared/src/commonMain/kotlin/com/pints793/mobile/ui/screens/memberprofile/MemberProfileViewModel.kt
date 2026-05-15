package com.pints793.mobile.ui.screens.memberprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pints793.mobile.api.UserApi
import com.pints793.mobile.di.ServiceLocator
import com.pints793.mobile.domain.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MemberProfileUiState(
    val profile: UserProfile? = null,
    val loading: Boolean = true,
    val error: String? = null,
)

class MemberProfileViewModel(
    private val userId: String,
    private val userApi: UserApi = ServiceLocator.userApi,
) : ViewModel() {

    private val _state = MutableStateFlow(MemberProfileUiState())
    val state: StateFlow<MemberProfileUiState> = _state.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null)
            try {
                _state.value = MemberProfileUiState(profile = userApi.getUserProfile(userId), loading = false)
            } catch (t: Throwable) {
                _state.value = _state.value.copy(loading = false, error = t.message)
            }
        }
    }
}

