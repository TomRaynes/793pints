package com.pints793.mobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import coil3.compose.AsyncImage
import com.pints793.mobile.ui.theme.PintsColors
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * Renders a circular profile avatar. Accepts either a remote URL, a `data:image/...;base64,...`
 * data URI, or a raw base64 string. Falls back to the supplied initial letter.
 */
@OptIn(ExperimentalEncodingApi::class)
@Composable
fun ProfileAvatar(
    imageData: String?,
    fallbackLetter: String,
    size: Dp = 96.dp,
    placeholderBg: Color = PintsColors.SurfaceAlt,
) {
    val model: Any? = remember(imageData) { decodeImageModel(imageData) }
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(placeholderBg),
        contentAlignment = Alignment.Center,
    ) {
        if (model != null) {
            AsyncImage(
                model = model,
                contentDescription = "Profile",
                modifier = Modifier.size(size).clip(CircleShape),
            )
        } else {
            Text(
                text = fallbackLetter.take(1).uppercase().ifEmpty { "?" },
                color = PintsColors.Primary,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge,
            )
        }
    }
}

@OptIn(ExperimentalEncodingApi::class)
internal fun decodeImageModel(imageData: String?): Any? {
    if (imageData.isNullOrBlank()) return null
    return when {
        imageData.startsWith("http://") || imageData.startsWith("https://") -> imageData
        imageData.startsWith("data:") -> {
            val payload = imageData.substringAfter(",", "")
            runCatching { Base64.decode(payload) }.getOrNull()
        }
        // Heuristic: treat anything else as a raw base64 blob.
        else -> runCatching { Base64.decode(imageData) }.getOrNull()
    }
}

