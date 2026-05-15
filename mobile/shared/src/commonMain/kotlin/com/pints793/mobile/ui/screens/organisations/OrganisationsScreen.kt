package com.pints793.mobile.ui.screens.organisations

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pints793.mobile.domain.EntityLabel
import com.pints793.mobile.ui.components.PageLayout
import com.pints793.mobile.ui.theme.PintsColors

@Composable
fun OrganisationsScreen(
    onBack: () -> Unit,
    onOpenOrganisation: (EntityLabel) -> Unit,
) {
    val vm = remember { OrganisationsViewModel() }
    val state by vm.state.collectAsStateWithLifecycle()
    var showNew by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf("") }

    PageLayout(title = "Organisations", onBack = onBack) {
        Text(
            "Select an organisation to manage its cellars and casks.",
            color = PintsColors.TextMuted,
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(Modifier.padding(8.dp))

        when {
            state.loading -> Text("Loading…", color = PintsColors.TextMuted)
            state.organisations.isEmpty() ->
                Text("🏢  No organisations yet. Create one to get started.", color = PintsColors.TextMuted)
            else -> state.organisations.forEach { org ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { onOpenOrganisation(org) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = PintsColors.Surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(org.name, color = PintsColors.Primary, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = PintsColors.TextMuted)
                    }
                }
                Spacer(Modifier.padding(4.dp))
            }
        }

        Spacer(Modifier.padding(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            ExtendedFloatingActionButton(
                onClick = { newName = ""; showNew = true },
                containerColor = PintsColors.Accent,
            ) { Text("+ New Organisation", fontWeight = FontWeight.SemiBold) }
        }

        state.error?.let {
            Spacer(Modifier.padding(4.dp))
            Text(it, color = PintsColors.Danger, style = MaterialTheme.typography.bodySmall)
        }
    }

    if (showNew) {
        AlertDialog(
            onDismissRequest = { if (!state.creating) showNew = false },
            confirmButton = {
                Button(
                    enabled = newName.isNotBlank() && !state.creating,
                    onClick = { vm.create(newName) { ok -> if (ok) showNew = false } },
                    colors = ButtonDefaults.buttonColors(containerColor = PintsColors.Accent),
                ) { Text(if (state.creating) "Creating…" else "Create") }
            },
            dismissButton = {
                TextButton(enabled = !state.creating, onClick = { showNew = false }) { Text("Cancel") }
            },
            title = { Text("New Organisation") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("Organisation Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            },
        )
    }
}

