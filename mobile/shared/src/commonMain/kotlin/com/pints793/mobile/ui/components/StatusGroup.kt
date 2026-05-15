package com.pints793.mobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pints793.mobile.domain.Cask
import com.pints793.mobile.domain.CaskState
import com.pints793.mobile.ui.theme.PintsColors

/** Mirrors `frontend/src/components/StatusGroup.tsx`. */
@Composable
fun StatusGroup(
    status: CaskState,
    casks: List<Cask>,
    organisationId: String?,
    cellarId: String?,
    onUpdateCask: (Cask) -> Unit,
    onRemoveCask: (String) -> Unit,
    onError: (Throwable) -> Unit,
) {
    if (casks.isEmpty()) return

    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(statusColor(status)),
            )
            Text(
                status.display,
                fontWeight = FontWeight.SemiBold,
                color = PintsColors.Primary,
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(Modifier.weight(1f))
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = PintsColors.SurfaceAlt,
            ) {
                Text(
                    text = casks.size.toString(),
                    color = PintsColors.TextMuted,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                )
            }
        }
        Spacer(Modifier.height(4.dp))
        casks.forEach { c ->
            CaskCard(
                cask = c,
                statusColor = statusColor(c.state),
                organisationId = organisationId,
                cellarId = cellarId,
                onUpdate = onUpdateCask,
                onRemove = onRemoveCask,
                onError = onError,
            )
            Spacer(Modifier.height(8.dp))
        }
    }
}

internal fun statusColor(state: CaskState): Color = when (state) {
    CaskState.Delivered    -> PintsColors.StatusDelivered
    CaskState.Racked       -> PintsColors.StatusRacked
    CaskState.Settled      -> PintsColors.StatusSettled
    CaskState.Vented       -> PintsColors.StatusVented
    CaskState.NeedsTap     -> PintsColors.StatusNeedsTap
    CaskState.Tapped       -> PintsColors.StatusTapped
    CaskState.ReadyToServe -> PintsColors.StatusReady
    CaskState.Pulling      -> PintsColors.StatusPulling
    CaskState.Tired        -> PintsColors.StatusTired
}

