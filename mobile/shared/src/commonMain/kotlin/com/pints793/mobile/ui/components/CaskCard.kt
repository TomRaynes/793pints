package com.pints793.mobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import com.pints793.mobile.api.CaskApi
import com.pints793.mobile.di.ServiceLocator
import com.pints793.mobile.domain.Cask
import com.pints793.mobile.domain.CaskState
import com.pints793.mobile.domain.UpdateCaskRequest
import com.pints793.mobile.domain.logic.CaskStateMachine
import com.pints793.mobile.ui.theme.PintsColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

/** Mirrors `frontend/src/components/CaskCard.tsx`. */
@Composable
fun CaskCard(
    cask: Cask,
    statusColor: Color,
    organisationId: String?,
    cellarId: String?,
    onUpdate: (Cask) -> Unit,
    onRemove: (String) -> Unit,
    onError: (Throwable) -> Unit,
    caskApi: CaskApi = ServiceLocator.caskApi,
) {
    var editOpen by remember { mutableStateOf(false) }
    var deleteOpen by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // 1Hz countdown
    val tickerFlow = remember(cask.caskId) {
        flow {
            while (true) {
                emit(Unit)
                delay(1000)
            }
        }
    }
    val tick by tickerFlow.collectAsState(initial = Unit)

    val cooldown = CaskStateMachine.cooldownHours(cask)
    val remaining = if (cooldown != null) {
        // touch tick so we re-compose every second
        tick
        CaskStateMachine.remainingMillis(cask)
    } else null

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Card(
            modifier = Modifier.weight(1f).clickable { editOpen = true },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = PintsColors.Surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .width(6.dp)
                        .height(56.dp)
                        .background(statusColor),
                )
                Column(
                    modifier = Modifier.weight(1f).padding(12.dp),
                ) {
                    Text(
                        cask.caskName,
                        color = PintsColors.Primary,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        cask.state.display,
                        color = PintsColors.TextMuted,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                if (remaining != null) {
                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .background(PintsColors.SurfaceAlt, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                    ) {
                        Text(
                            text = "⏱ " + formatRemaining(remaining),
                            color = PintsColors.Accent,
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }
        }
        IconButton(onClick = { deleteOpen = true }) {
            Text("✕", color = PintsColors.Danger, fontWeight = FontWeight.Bold)
        }
    }

    if (deleteOpen) {
        DeleteCaskDialog(
            cask = cask,
            onDismiss = { deleteOpen = false },
            onConfirm = {
                if (organisationId == null || cellarId == null) return@DeleteCaskDialog
                onRemove(cask.caskId) // optimistic
                deleteOpen = false
                scope.launch {
                    runCatching { caskApi.remove(organisationId, cellarId, cask.caskId) }
                        .onFailure(onError)
                }
            },
        )
    }

    if (editOpen) {
        EditCaskDialog(
            cask = cask,
            onDismiss = { editOpen = false },
            onSave = { req ->
                if (organisationId == null || cellarId == null) return@EditCaskDialog
                editOpen = false
                kotlinx.coroutines.GlobalScope.launch {
                    runCatching {
                        caskApi.update(
                            req.copy(
                                organisationId = organisationId,
                                cellarId = cellarId,
                                caskId = cask.caskId,
                            )
                        )
                    }.onSuccess { onUpdate(it) }.onFailure(onError)
                }
            },
        )
    }
}

@Composable
private fun DeleteCaskDialog(cask: Cask, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = PintsColors.Danger),
            ) { Text("Delete") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
        title = { Text("Delete Cask") },
        text = {
            Text("Are you sure you want to delete \"${cask.caskName}\"? This action cannot be undone.")
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditCaskDialog(
    cask: Cask,
    onDismiss: () -> Unit,
    onSave: (UpdateCaskRequest) -> Unit,
) {
    var name by remember(cask) { mutableStateOf(cask.caskName) }
    var state by remember(cask) { mutableStateOf(cask.state) }
    var rack by remember(cask) { mutableStateOf(cask.rackCooldownHours?.toString().orEmpty()) }
    var vent by remember(cask) { mutableStateOf(cask.ventCooldownHours?.toString().orEmpty()) }
    var tap by remember(cask) { mutableStateOf(cask.tapCooldownHours?.toString().orEmpty()) }
    var pull by remember(cask) { mutableStateOf(cask.pullingPeriodHours?.toString().orEmpty()) }
    var stateMenuOpen by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                enabled = name.isNotBlank(),
                onClick = {
                    onSave(
                        UpdateCaskRequest(
                            organisationId = "",
                            cellarId = "",
                            caskId = cask.caskId,
                            caskName = name.trim(),
                            state = state,
                            rackCooldownHours = rack.takeIf { it.isNotBlank() },
                            ventCooldownHours = vent.takeIf { it.isNotBlank() },
                            tapCooldownHours = tap.takeIf { it.isNotBlank() },
                            pullingPeriodHours = pull.takeIf { it.isNotBlank() },
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = PintsColors.Accent),
            ) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
        title = { Text("Edit Cask") },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                ExposedDropdownMenuBox(
                    expanded = stateMenuOpen,
                    onExpandedChange = { stateMenuOpen = !stateMenuOpen },
                ) {
                    OutlinedTextField(
                        value = state.display,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("State") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = stateMenuOpen) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                    )
                    DropdownMenu(expanded = stateMenuOpen, onDismissRequest = { stateMenuOpen = false }) {
                        CaskState.entriesInOrder.forEach { s ->
                            DropdownMenuItem(text = { Text(s.display) }, onClick = {
                                state = s; stateMenuOpen = false
                            })
                        }
                    }
                }
                NumberRow("Racking Cooldown (hours)", rack) { rack = it }
                NumberRow("Venting Cooldown (hours)", vent) { vent = it }
                NumberRow("Tapping Cooldown (hours)", tap) { tap = it }
                NumberRow("Pulling Period (hours)", pull) { pull = it }
            }
        },
    )
}

@Composable
private fun NumberRow(label: String, value: String, onChange: (String) -> Unit) {
    Spacer(Modifier.height(8.dp))
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = Modifier.fillMaxWidth(),
    )
}

private fun formatRemaining(ms: Long): String {
    val totalSeconds = (ms.coerceAtLeast(0) / 1000).toInt()
    val h = totalSeconds / 3600
    val m = (totalSeconds % 3600) / 60
    val s = totalSeconds % 60
    return "${h.pad()}:${m.pad()}:${s.pad()}"
}

private fun Int.pad(): String = if (this < 10) "0$this" else toString()



