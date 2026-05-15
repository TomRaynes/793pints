package com.pints793.mobile.ui.screens.editprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pints793.mobile.api.UserApi
import com.pints793.mobile.di.ServiceLocator
import com.pints793.mobile.domain.UpdateProfileRequest
import com.pints793.mobile.domain.UserProfile
import com.pints793.mobile.image.PickedImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class EditProfileUiState(
    val profile: UserProfile? = null,
    val name: String = "",
    val bio: String = "",
    val loading: Boolean = true,
    val saving: Boolean = false,
    val uploading: Boolean = false,
    val savedFlash: Boolean = false,
    val error: String? = null,
) {
    val hasChanges: Boolean
        get() = profile != null && (name != (profile.name ?: "") || bio != (profile.bio ?: ""))
}

class EditProfileViewModel(
    private val userApi: UserApi = ServiceLocator.userApi,
) : ViewModel() {

    private val _state = MutableStateFlow(EditProfileUiState())
    val state: StateFlow<EditProfileUiState> = _state.asStateFlow()

    init { load() }

    private fun load() {
        viewModelScope.launch {
            try {
                val p = userApi.getProfile()
                _state.value = EditProfileUiState(
                    profile = p,
                    name = p.name.orEmpty(),
                    bio = p.bio.orEmpty(),
                    loading = false,
                )
            } catch (t: Throwable) {
                _state.value = _state.value.copy(loading = false, error = t.message)
            }
        }
    }

    fun setName(v: String) { _state.value = _state.value.copy(name = v) }
    fun setBio(v: String) { _state.value = _state.value.copy(bio = v) }

    fun save() {
        if (_state.value.saving) return
        viewModelScope.launch {
            _state.value = _state.value.copy(saving = true, savedFlash = false, error = null)
            try {
                val updated = userApi.updateProfile(
                    UpdateProfileRequest(name = _state.value.name, bio = _state.value.bio)
                )
                _state.value = _state.value.copy(
                    profile = updated,
                    name = updated.name.orEmpty(),
                    bio = updated.bio.orEmpty(),
                    saving = false,
                    savedFlash = true,
                )
                delay(2000)
                _state.value = _state.value.copy(savedFlash = false)
            } catch (t: Throwable) {
                _state.value = _state.value.copy(saving = false, error = t.message)
            }
        }
    }

    fun uploadPicture(picked: PickedImage) {
        if (picked.bytes.size > 2 * 1024 * 1024) {
            _state.value = _state.value.copy(error = "Image must be under 2 MB.")
            return
        }
        viewModelScope.launch {
            _state.value = _state.value.copy(uploading = true, error = null)
            try {
                val updated = userApi.uploadProfilePicture(picked.bytes, picked.mimeType, picked.filename)
                _state.value = _state.value.copy(profile = updated, uploading = false)
            } catch (t: Throwable) {
                _state.value = _state.value.copy(uploading = false, error = t.message)
            }
        }
    }
}

