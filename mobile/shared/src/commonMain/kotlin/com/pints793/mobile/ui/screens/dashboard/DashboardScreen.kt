package com.pints793.mobile.ui.screens.dashboard

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import com.pints793.mobile.domain.PinnedCellarInfo
import com.pints793.mobile.ui.components.PageLayout
import com.pints793.mobile.ui.theme.PintsColors

@Composable
fun DashboardScreen(
    onOpenOrganisations: () -> Unit,
    onOpenInvitations: () -> Unit,
    onOpenProfile: () -> Unit,
    onOpenPinnedCellar: (PinnedCellarInfo) -> Unit,
) {
    val vm = remember { DashboardViewModel() }
    val state by vm.state.collectAsStateWithLifecycle()

    PageLayout(title = "Dashboard") {
        Text("Welcome back!", color = PintsColors.TextMuted, style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(20.dp))

        if (state.pinnedCellars.isNotEmpty()) {
            Text("📌 PINNED CELLARS", color = PintsColors.TextMuted, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
            Spacer(Modifier.height(8.dp))
            state.pinnedCellars.forEach { p ->
                PinnedCellarCard(p, onClick = { onOpenPinnedCellar(p) })
                Spacer(Modifier.height(8.dp))
            }
            Spacer(Modifier.height(20.dp))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            DashboardTile("👤", "Profile", Modifier.weight(1f), onOpenProfile)
            DashboardTile("✉️", "Invitations", Modifier.weight(1f), onOpenInvitations)
        }
        Spacer(Modifier.height(12.dp))
        DashboardTile("🏢", "Organisations", Modifier.fillMaxWidth(), onOpenOrganisations)

        state.errorMessage?.let {
            Spacer(Modifier.height(16.dp))
            Text(it, color = PintsColors.Danger, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun PinnedCellarCard(p: PinnedCellarInfo, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = PintsColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("🛢️", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(end = 12.dp))
            Column(Modifier.weight(1f)) {
                Text(p.cellarName, color = PintsColors.Primary, fontWeight = FontWeight.SemiBold)
                Text(p.organisationName, color = PintsColors.TextMuted, style = MaterialTheme.typography.bodySmall)
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = PintsColors.TextMuted)
        }
    }
}

@Composable
private fun DashboardTile(emoji: String, label: String, modifier: Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.height(112.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PintsColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(emoji, style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(4.dp))
            Text(label, fontWeight = FontWeight.SemiBold, color = PintsColors.Primary)
        }
    }
}

