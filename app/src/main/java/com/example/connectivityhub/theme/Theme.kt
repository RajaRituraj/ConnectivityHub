package com.example.connectivityhub.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    // Primary — Cyan
    primary                = CyanPrimary,
    onPrimary              = NavyBackground,
    primaryContainer       = Color(0xFF003A4A),
    onPrimaryContainer     = CyanPrimaryLight,

    // Secondary — Violet
    secondary              = VioletSecondary,
    onSecondary            = Color(0xFFFFFFFF),
    secondaryContainer     = Color(0xFF2A1970),
    onSecondaryContainer   = VioletLight,

    // Tertiary — Mint
    tertiary               = MintTertiary,
    onTertiary             = NavyBackground,
    tertiaryContainer      = Color(0xFF003D2A),
    onTertiaryContainer    = MintLight,

    // Error
    error                  = ErrorRed,
    onError                = Color(0xFF4A0010),
    errorContainer         = Color(0xFF6B0018),
    onErrorContainer       = Color(0xFFFFDAD6),

    // Backgrounds
    background             = NavyBackground,
    onBackground           = TextPrimary,
    surface                = NavySurface,
    onSurface              = TextPrimary,
    surfaceVariant         = NavySurfaceVariant,
    onSurfaceVariant       = TextSecondary,

    // Outline
    outline                = BorderSubtle,
    outlineVariant         = BorderActive,

    // Inverse
    inverseSurface         = TextPrimary,
    inverseOnSurface       = NavyBackground,
    inversePrimary         = CyanPrimaryDark,
)

@Composable
fun ConnectivityHubTheme(
    content: @Composable () -> Unit,
) {
    // Always use dark theme — the app is designed as a dark-first experience
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content,
    )
}
