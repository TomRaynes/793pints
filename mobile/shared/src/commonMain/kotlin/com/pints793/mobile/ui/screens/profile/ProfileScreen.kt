package com.pints793.mobile.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
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
fun ProfileScreen(
    onBack: () -> Unit,
    onEdit: () -> Unit,
) {
    val vm = remember { ProfileViewModel() }
    val state by vm.state.collectAsStateWithLifecycle()

    PageLayout(
        title = "Profile",
        onBack = onBack,
        actions = { IconButton(onClick = onEdit) { Text("✎", fontWeight = FontWeight.Bold) } },
    ) {
        when {
            state.loading -> Text("Loading…", color = PintsColors.TextMuted)
            state.profile == null -> Text("Could not load profile.", color = PintsColors.Danger)
            else -> {
                val p = state.profile!!
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    ProfileAvatar(
                        imageData = p.profilePicture,
                        fallbackLetter = (p.name ?: p.username).take(1),
                        size = 88.dp,
                    )
                    Column {
                        Text(p.username, color = PintsColors.Primary, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
                        Text(p.email, color = PintsColors.TextMuted, style = MaterialTheme.typography.bodySmall)
                    }
                }
                Spacer(Modifier.height(16.dp))
                ProfileField("Display Name", p.name)
                Spacer(Modifier.height(8.dp))
                ProfileField("Bio", p.bio)
            }
        }
        state.error?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = PintsColors.Danger, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun ProfileField(label: String, value: String?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = PintsColors.Surface),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(label, color = PintsColors.TextMuted, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(4.dp))
            Text(
                value?.takeIf { it.isNotBlank() } ?: "Not set",
                color = if (value.isNullOrBlank()) PintsColors.TextMuted else PintsColors.Primary,
            )
        }
    }
}

