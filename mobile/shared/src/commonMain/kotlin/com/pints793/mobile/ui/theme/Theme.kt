package com.pints793.mobile.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Mirrors frontend/src/index.css — warm parchment + burnished amber.
object PintsColors {
    val Bg          = Color(0xFFF5F1EA)
    val Surface     = Color(0xFFFFFFFF)
    val SurfaceAlt  = Color(0xFFF7F3EC)
    val Border      = Color(0xFFE4DCCF)
    val Primary     = Color(0xFF1F1A17)
    val Accent      = Color(0xFFB8772A)
    val AccentHover = Color(0xFFA3661F)
    val TextMuted   = Color(0xFF8A7F72)
    val Danger      = Color(0xFFB3382C)
    val Success     = Color(0xFF2F7A48)

    // Cask status palette
    val StatusDelivered = Color(0xFF8A7F72)
    val StatusRacked    = Color(0xFFA3661F)
    val StatusSettled   = Color(0xFF6E7A3C)
    val StatusVented    = Color(0xFF2C6E9B)
    val StatusNeedsTap  = Color(0xFFC2901C)
    val StatusTapped    = Color(0xFFD2691E)
    val StatusReady     = Color(0xFF2F7A48)
    val StatusPulling   = Color(0xFF2C6E9B)
    val StatusTired     = Color(0xFF8A7F72)
}

private val LightColors = lightColorScheme(
    primary = PintsColors.Accent,
    onPrimary = Color.White,
    secondary = PintsColors.Primary,
    background = PintsColors.Bg,
    surface = PintsColors.Surface,
    onSurface = PintsColors.Primary,
    error = PintsColors.Danger,
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFD8924A),
    onPrimary = Color.White,
    background = Color(0xFF1A1714),
    surface = Color(0xFF2A231D),
    onSurface = Color(0xFFF0E8DB),
    error = Color(0xFFE06B5E),
)

@Composable
fun PintsTheme(useDark: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (useDark) DarkColors else LightColors,
        content = content,
    )
}

