package com.pints793.mobile.ui.screens.cellar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pints793.mobile.api.CaskApi
import com.pints793.mobile.api.CellarApi
import com.pints793.mobile.api.UserApi
import com.pints793.mobile.di.ServiceLocator
import com.pints793.mobile.domain.Cask
import com.pints793.mobile.domain.CaskState
import com.pints793.mobile.domain.CellarConfig
import com.pints793.mobile.domain.CellarConfigField
import com.pints793.mobile.domain.UpdateCellarConfigRequest
import com.pints793.mobile.domain.logic.CaskStateMachine
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CellarUiState(
    val casks: List<Cask> = emptyList(),
    val isPinned: Boolean = false,
    val togglingPin: Boolean = false,
    val creating: Boolean = false,
    val loading: Boolean = true,
    val error: String? = null,

    val showSettings: Boolean = false,
    val settingsLoading: Boolean = false,
    val savingSettings: Boolean = false,
    val settingsSaved: Boolean = false,
    val rackDefault: String = "",
    val ventDefault: String = "",
    val tapDefault: String = "",
    val pullDefault: String = "",
    val applyRackAll: Boolean = false,
    val applyVentAll: Boolean = false,
    val applyTapAll: Boolean = false,
    val applyPullAll: Boolean = false,
)

class CellarViewModel(
    private val orgId: String,
    private val cellarId: String,
    private val caskApi: CaskApi = ServiceLocator.caskApi,
    private val cellarApi: CellarApi = ServiceLocator.cellarApi,
    private val userApi: UserApi = ServiceLocator.userApi,
) : ViewModel() {

    private val _state = MutableStateFlow(CellarUiState())
    val state: StateFlow<CellarUiState> = _state.asStateFlow()

    init {
        load()
        refreshPinned()
        startCooldownTicker()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null)
            try {
                val list = caskApi.getAll(orgId, cellarId)
                _state.value = _state.value.copy(casks = list, loading = false)
            } catch (t: Throwable) {
                _state.value = _state.value.copy(loading = false, error = t.message)
            }
        }
    }

    private fun refreshPinned() {
        viewModelScope.launch {
            runCatching { userApi.getPinnedCellars() }
                .onSuccess { pinned ->
                    _state.value = _state.value.copy(isPinned = pinned.any { it.cellarId == cellarId })
                }
        }
    }

    fun togglePin() {
        if (_state.value.togglingPin) return
        viewModelScope.launch {
            _state.value = _state.value.copy(togglingPin = true)
            val pinned = _state.value.isPinned
            val ok = runCatching {
                if (pinned) userApi.unpinCellar(cellarId) else userApi.pinCellar(cellarId)
            }.isSuccess
            _state.value = _state.value.copy(
                togglingPin = false,
                isPinned = if (ok) !pinned else pinned,
            )
        }
    }

    fun createCask(name: String, onDone: (Boolean) -> Unit) {
        val trimmed = name.trim()
        if (trimmed.isEmpty() || _state.value.creating) { onDone(false); return }
        viewModelScope.launch {
            _state.value = _state.value.copy(creating = true, error = null)
            val ok = runCatching { caskApi.create(orgId, cellarId, trimmed, CaskState.Delivered) }
                .onFailure { _state.value = _state.value.copy(error = it.message) }
                .isSuccess
            _state.value = _state.value.copy(creating = false)
            if (ok) load()
            onDone(ok)
        }
    }

    fun replaceCask(updated: Cask) {
        _state.value = _state.value.copy(
            casks = _state.value.casks.map { if (it.caskId == updated.caskId) updated else it }
        )
    }

    fun removeCaskLocal(caskId: String) {
        _state.value = _state.value.copy(casks = _state.value.casks.filterNot { it.caskId == caskId })
    }

    /** 1 Hz ticker that auto-advances any cask whose cooldown has elapsed. */
    private fun startCooldownTicker() {
        viewModelScope.launch {
            while (true) {
                delay(1000)
                val advanced = _state.value.casks.map { c ->
                    val r = CaskStateMachine.remainingMillis(c) ?: return@map c
                    if (r > 0) c else CaskStateMachine.refresh(c)
                }
                if (advanced != _state.value.casks) {
                    _state.value = _state.value.copy(casks = advanced)
                }
            }
        }
    }

    // ── Settings dialog ──
    fun openSettings() {
        _state.value = _state.value.copy(
            showSettings = true,
            settingsLoading = true,
            settingsSaved = false,
            applyRackAll = false, applyVentAll = false, applyTapAll = false, applyPullAll = false,
        )
        viewModelScope.launch {
            try {
                val cfg = cellarApi.getConfig(cellarId)
                _state.value = _state.value.copy(
                    settingsLoading = false,
                    rackDefault = cfg.rackCooldownDefault.toString(),
                    ventDefault = cfg.ventCooldownDefault.toString(),
                    tapDefault  = cfg.tapCooldownDefault.toString(),
                    pullDefault = cfg.pullingPeriodDefault.toString(),
                )
            } catch (t: Throwable) {
                _state.value = _state.value.copy(settingsLoading = false, error = t.message)
            }
        }
    }
    fun closeSettings() {
        if (_state.value.savingSettings) return
        _state.value = _state.value.copy(showSettings = false)
    }
    fun setRackDefault(v: String) { _state.value = _state.value.copy(rackDefault = v) }
    fun setVentDefault(v: String) { _state.value = _state.value.copy(ventDefault = v) }
    fun setTapDefault (v: String) { _state.value = _state.value.copy(tapDefault  = v) }
    fun setPullDefault(v: String) { _state.value = _state.value.copy(pullDefault = v) }
    fun setApplyRackAll(v: Boolean) { _state.value = _state.value.copy(applyRackAll = v) }
    fun setApplyVentAll(v: Boolean) { _state.value = _state.value.copy(applyVentAll = v) }
    fun setApplyTapAll (v: Boolean) { _state.value = _state.value.copy(applyTapAll  = v) }
    fun setApplyPullAll(v: Boolean) { _state.value = _state.value.copy(applyPullAll = v) }

    fun saveSettings() {
        if (_state.value.savingSettings) return
        viewModelScope.launch {
            _state.value = _state.value.copy(savingSettings = true, settingsSaved = false)
            val req = UpdateCellarConfigRequest(
                rackCooldownDefault  = CellarConfigField(_state.value.rackDefault.toDoubleOrNull() ?: 0.0, _state.value.applyRackAll),
                ventCooldownDefault  = CellarConfigField(_state.value.ventDefault.toDoubleOrNull() ?: 0.0, _state.value.applyVentAll),
                tapCooldownDefault   = CellarConfigField(_state.value.tapDefault .toDoubleOrNull() ?: 0.0, _state.value.applyTapAll),
                pullingPeriodDefault = CellarConfigField(_state.value.pullDefault.toDoubleOrNull() ?: 0.0, _state.value.applyPullAll),
            )
            val ok = runCatching { cellarApi.updateConfig(cellarId, req) }
                .onFailure { _state.value = _state.value.copy(error = it.message) }
                .isSuccess
            _state.value = _state.value.copy(savingSettings = false, settingsSaved = ok)
            if (ok) {
                if (_state.value.applyRackAll || _state.value.applyVentAll
                    || _state.value.applyTapAll || _state.value.applyPullAll
                ) load()
                delay(2000)
                _state.value = _state.value.copy(settingsSaved = false)
            }
        }
    }

    @Suppress("unused")
    fun emptyConfig() = CellarConfig() // ensures kotlinx.serialization includes it
}

