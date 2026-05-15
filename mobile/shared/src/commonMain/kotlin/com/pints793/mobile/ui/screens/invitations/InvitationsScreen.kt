package com.pints793.mobile.ui.screens.invitations

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pints793.mobile.ui.components.PageLayout
import com.pints793.mobile.ui.components.ProfileAvatar
import com.pints793.mobile.ui.theme.PintsColors

@Composable
fun InvitationsScreen(onBack: () -> Unit) {
    val vm = remember { InvitationsViewModel() }
    val state by vm.state.collectAsStateWithLifecycle()

    PageLayout(title = "Invitations", onBack = onBack) {
        Text(
            "Review and accept invitations to join organisations.",
            color = PintsColors.TextMuted,
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(Modifier.height(16.dp))

        when {
            state.loading -> Text("Loading…", color = PintsColors.TextMuted)
            state.invitations.isEmpty() -> Text(
                "✉️  No pending invitations.",
                color = PintsColors.TextMuted,
                modifier = Modifier.padding(vertical = 24.dp),
            )
            else -> state.invitations.forEach { inv ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = PintsColors.Surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        ProfileAvatar(
                            imageData = state.senderImages[inv.id],
                            fallbackLetter = inv.senderUsername.orEmpty().ifEmpty { "?" },
                            size = 44.dp,
                        )
                        Column(Modifier.weight(1f)) {
                            Text(
                                inv.senderUsername.orEmpty(),
                                color = PintsColors.Primary,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Text(
                                "invited you to ${inv.organisationName.orEmpty()}",
                                color = PintsColors.TextMuted,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                        Button(
                            onClick = { vm.accept(inv) },
                            colors = ButtonDefaults.buttonColors(containerColor = PintsColors.Success),
                        ) { Text("Accept") }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }

        state.error?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = PintsColors.Danger, style = MaterialTheme.typography.bodySmall)
        }
    }
}

