package com.pints793.mobile.ui.screens.cellar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pints793.mobile.domain.CaskState
import com.pints793.mobile.ui.components.PageLayout
import com.pints793.mobile.ui.components.StatusGroup
import com.pints793.mobile.ui.theme.PintsColors

@Composable
fun CellarScreen(
    orgId: String,
    orgName: String,
    cellarId: String,
    cellarName: String,
    onBack: () -> Unit,
) {
    val vm = remember(cellarId) { CellarViewModel(orgId, cellarId) }
    val state by vm.state.collectAsStateWithLifecycle()

    var showNew by remember { mutableStateOf(false) }
    var newCask by remember { mutableStateOf("") }

    PageLayout(
        title = "$orgName · $cellarName",
        onBack = onBack,
        actions = {
            IconButton(
                onClick = vm::togglePin,
                enabled = !state.togglingPin,
            ) {
                Text(if (state.isPinned) "📌" else "📍")
            }
            IconButton(onClick = vm::openSettings) { Text("⚙") }
        },
    ) {
        val total = state.casks.size
        Text(
            "$total cask${if (total != 1) "s" else ""} in this cellar",
            color = PintsColors.TextMuted,
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(Modifier.height(12.dp))

        when {
            state.loading -> Text("Loading…", color = PintsColors.TextMuted)
            state.casks.isEmpty() ->
                Text("🛢️  No casks in this cellar yet. Add one to get started.", color = PintsColors.TextMuted)
            else -> CaskState.entriesInOrder.forEach { s ->
                StatusGroup(
                    status = s,
                    casks = state.casks.filter { it.state == s },
                    organisationId = orgId,
                    cellarId = cellarId,
                    onUpdateCask = vm::replaceCask,
                    onRemoveCask = vm::removeCaskLocal,
                    onError = { /* surface in state if desired */ },
                )
            }
        }

        Spacer(Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            ExtendedFloatingActionButton(
                onClick = { newCask = ""; showNew = true },
                containerColor = PintsColors.Accent,
            ) { Text("+ New Cask", fontWeight = FontWeight.SemiBold) }
        }

        state.error?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = PintsColors.Danger, style = MaterialTheme.typography.bodySmall)
        }
    }

    if (showNew) {
        AlertDialog(
            onDismissRequest = { if (!state.creating) showNew = false },
            confirmButton = {
                Button(
                    enabled = newCask.isNotBlank() && !state.creating,
                    onClick = { vm.createCask(newCask) { ok -> if (ok) showNew = false } },
                    colors = ButtonDefaults.buttonColors(containerColor = PintsColors.Accent),
                ) { Text(if (state.creating) "Creating…" else "Create") }
            },
            dismissButton = { TextButton(enabled = !state.creating, onClick = { showNew = false }) { Text("Cancel") } },
            title = { Text("New Cask") },
            text = {
                OutlinedTextField(
                    value = newCask,
                    onValueChange = { newCask = it },
                    label = { Text("Cask Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            },
        )
    }

    if (state.showSettings) {
        AlertDialog(
            onDismissRequest = vm::closeSettings,
            confirmButton = {
                Button(
                    enabled = !state.savingSettings,
                    onClick = vm::saveSettings,
                    colors = ButtonDefaults.buttonColors(containerColor = PintsColors.Accent),
                ) { Text(if (state.savingSettings) "Saving…" else "Save") }
            },
            dismissButton = { TextButton(enabled = !state.savingSettings, onClick = vm::closeSettings) { Text("Close") } },
            title = { Text("Cellar Settings") },
            text = {
                Column {
                    if (state.settingsLoading) {
                        Text("Loading…", color = PintsColors.TextMuted)
                    } else {
                        if (state.settingsSaved) {
                            Text("✓ Saved", color = PintsColors.Success)
                            Spacer(Modifier.height(4.dp))
                        }
                        ConfigField("Racking Cooldown Default (hours)", orgName, state.rackDefault, vm::setRackDefault, state.applyRackAll, vm::setApplyRackAll, state.savingSettings)
                        ConfigField("Venting Cooldown Default (hours)", orgName, state.ventDefault, vm::setVentDefault, state.applyVentAll, vm::setApplyVentAll, state.savingSettings)
                        ConfigField("Tapping Cooldown Default (hours)", orgName, state.tapDefault,  vm::setTapDefault,  state.applyTapAll,  vm::setApplyTapAll,  state.savingSettings)
                        ConfigField("Pulling Period Default (hours)",  orgName, state.pullDefault, vm::setPullDefault, state.applyPullAll, vm::setApplyPullAll, state.savingSettings)
                    }
                }
            },
        )
    }
}

@Composable
private fun ConfigField(
    label: String,
    orgName: String,
    value: String,
    onValue: (String) -> Unit,
    applyAll: Boolean,
    onApplyAll: (Boolean) -> Unit,
    disabled: Boolean,
) {
    Spacer(Modifier.height(8.dp))
    OutlinedTextField(
        value = value,
        onValueChange = onValue,
        label = { Text(label) },
        singleLine = true,
        enabled = !disabled,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = Modifier.fillMaxWidth(),
    )
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
        Checkbox(checked = applyAll, onCheckedChange = onApplyAll, enabled = !disabled)
        Text("Apply to all cellars in $orgName", color = PintsColors.TextMuted, style = MaterialTheme.typography.bodySmall)
    }
}

