package com.pints793.mobile.ui.screens.organisation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.OutlinedButton
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
import com.pints793.mobile.ui.components.ProfileAvatar
import com.pints793.mobile.ui.theme.PintsColors

@Composable
fun OrganisationScreen(
    orgId: String,
    orgName: String,
    onBack: () -> Unit,
    onOpenCellar: (EntityLabel) -> Unit,
    onOpenMember: (String) -> Unit,
) {
    val vm = remember(orgId) { OrganisationViewModel(orgId) }
    val state by vm.state.collectAsStateWithLifecycle()

    var showInvite by remember { mutableStateOf(false) }
    var inviteId by remember { mutableStateOf("") }
    var showNewCellar by remember { mutableStateOf(false) }
    var newCellar by remember { mutableStateOf("") }
    var showMembers by remember { mutableStateOf(false) }

    PageLayout(
        title = orgName,
        onBack = onBack,
        actions = {
            TextButton(onClick = { showMembers = true }) { Text("Members", color = PintsColors.Accent) }
        },
    ) {
        Text(
            "Manage cellars within this organisation.",
            color = PintsColors.TextMuted,
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(Modifier.height(16.dp))

        when {
            state.loading -> Text("Loading…", color = PintsColors.TextMuted)
            state.cellars.isEmpty() ->
                Text("🏗️  No cellars yet. Create one to start tracking casks.", color = PintsColors.TextMuted)
            else -> state.cellars.forEach { c ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable {
                        vm.writeCache(); onOpenCellar(c)
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = PintsColors.Surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(c.name, color = PintsColors.Primary, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = PintsColors.TextMuted)
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }

        Spacer(Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            ExtendedFloatingActionButton(
                onClick = { newCellar = ""; showNewCellar = true },
                containerColor = PintsColors.Accent,
            ) { Text("+ New Cellar", fontWeight = FontWeight.SemiBold) }
        }

        state.error?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = PintsColors.Danger, style = MaterialTheme.typography.bodySmall)
        }
    }

    // ── New Cellar dialog ──
    if (showNewCellar) {
        AlertDialog(
            onDismissRequest = { if (!state.creatingCellar) showNewCellar = false },
            confirmButton = {
                Button(
                    enabled = newCellar.isNotBlank() && !state.creatingCellar,
                    onClick = { vm.createCellar(newCellar) { ok -> if (ok) showNewCellar = false } },
                    colors = ButtonDefaults.buttonColors(containerColor = PintsColors.Accent),
                ) { Text(if (state.creatingCellar) "Creating…" else "Create") }
            },
            dismissButton = { TextButton(enabled = !state.creatingCellar, onClick = { showNewCellar = false }) { Text("Cancel") } },
            title = { Text("New Cellar") },
            text = {
                OutlinedTextField(
                    value = newCellar,
                    onValueChange = { newCellar = it },
                    label = { Text("Cellar Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            },
        )
    }

    // ── Members dialog ──
    if (showMembers) {
        AlertDialog(
            onDismissRequest = { showMembers = false },
            confirmButton = {
                if (state.accessLevel == "Owner") {
                    Button(
                        onClick = { showMembers = false; inviteId = ""; showInvite = true },
                        colors = ButtonDefaults.buttonColors(containerColor = PintsColors.Accent),
                    ) { Text("Invite Member") }
                } else {
                    TextButton(onClick = { showMembers = false }) { Text("Close") }
                }
            },
            dismissButton = if (state.accessLevel == "Owner") {
                { TextButton(onClick = { showMembers = false }) { Text("Close") } }
            } else null,
            title = { Text("Members") },
            text = {
                Column {
                    state.inviteSuccess?.let { msg ->
                        Text("✓ $msg", color = PintsColors.Success, style = MaterialTheme.typography.bodySmall)
                        Spacer(Modifier.height(8.dp))
                    }
                    val members = state.members
                    if (members == null) {
                        Text("Loading…", color = PintsColors.TextMuted)
                    } else {
                        members.admins.forEach { (id, username) ->
                            MemberRow(id, username, isAdmin = true, image = state.memberImages[id]) {
                                showMembers = false; onOpenMember(id)
                            }
                        }
                        members.members.forEach { (id, username) ->
                            MemberRow(id, username, isAdmin = false, image = state.memberImages[id]) {
                                showMembers = false; onOpenMember(id)
                            }
                        }
                    }
                }
            },
        )
    }

    // ── Invite dialog ──
    if (showInvite) {
        AlertDialog(
            onDismissRequest = { if (!state.inviting) showInvite = false },
            confirmButton = {
                Button(
                    enabled = inviteId.isNotBlank() && !state.inviting,
                    onClick = {
                        vm.invite(inviteId) { ok ->
                            if (ok) { showInvite = false; showMembers = true }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PintsColors.Accent),
                ) { Text(if (state.inviting) "Inviting…" else "Send Invite") }
            },
            dismissButton = {
                OutlinedButton(enabled = !state.inviting, onClick = {
                    showInvite = false; showMembers = true
                }) { Text("Cancel") }
            },
            title = { Text("Invite Member") },
            text = {
                OutlinedTextField(
                    value = inviteId,
                    onValueChange = { inviteId = it },
                    label = { Text("Username or Email") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            },
        )
    }
}

@Composable
private fun MemberRow(id: String, username: String, isAdmin: Boolean, image: String?, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        ProfileAvatar(imageData = image, fallbackLetter = username.take(1), size = 36.dp)
        Text(username, color = PintsColors.Primary, modifier = Modifier.weight(1f))
        if (isAdmin) {
            Text(
                "Admin",
                color = PintsColors.Accent,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}

