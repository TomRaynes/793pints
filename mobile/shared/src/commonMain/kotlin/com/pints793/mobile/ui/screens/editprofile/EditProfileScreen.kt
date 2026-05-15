package com.pints793.mobile.ui.screens.editprofile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pints793.mobile.image.rememberImagePicker
import com.pints793.mobile.ui.components.PageLayout
import com.pints793.mobile.ui.components.ProfileAvatar
import com.pints793.mobile.ui.theme.PintsColors

@Composable
fun EditProfileScreen(onBack: () -> Unit) {
    val vm = remember { EditProfileViewModel() }
    val state by vm.state.collectAsStateWithLifecycle()
    val launchPicker = rememberImagePicker { picked -> picked?.let(vm::uploadPicture) }

    PageLayout(title = "Edit Profile", onBack = onBack) {
        if (state.loading) {
            Text("Loading…", color = PintsColors.TextMuted); return@PageLayout
        }
        val p = state.profile
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(modifier = Modifier.clickable(enabled = !state.uploading) { launchPicker() }) {
                ProfileAvatar(
                    imageData = p?.profilePicture,
                    fallbackLetter = (p?.name ?: p?.username ?: "?").take(1),
                    size = 88.dp,
                )
            }
            Column {
                Text(p?.username.orEmpty(), color = PintsColors.Primary, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
                Text(p?.email.orEmpty(), color = PintsColors.TextMuted, style = MaterialTheme.typography.bodySmall)
                Text(
                    if (state.uploading) "Uploading…" else "Tap avatar to change",
                    color = PintsColors.Accent,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }

        Spacer(Modifier.height(20.dp))
        OutlinedTextField(
            value = state.name,
            onValueChange = vm::setName,
            label = { Text("Display Name") },
            singleLine = true,
            enabled = !state.saving,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = state.bio,
            onValueChange = vm::setBio,
            label = { Text("Bio") },
            enabled = !state.saving,
            minLines = 3,
            maxLines = 6,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (state.savedFlash) {
                Text("✓ Saved", color = PintsColors.Success, modifier = Modifier.padding(end = 12.dp))
            }
            Button(
                onClick = vm::save,
                enabled = state.hasChanges && !state.saving,
                colors = ButtonDefaults.buttonColors(containerColor = PintsColors.Accent),
            ) {
                Text(if (state.saving) "Saving…" else "Save Changes")
            }
        }

        state.error?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = PintsColors.Danger, style = MaterialTheme.typography.bodySmall)
        }
    }
}

