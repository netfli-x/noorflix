package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val NoorFlixColorScheme = darkColorScheme(
    primary = NoorGold,
    onPrimary = NoorPrimaryBackground,
    primaryContainer = NoorCardBackground,
    onPrimaryContainer = NoorGoldLight,

    secondary = NoorPurple,
    onSecondary = NoorTextPrimary,
    secondaryContainer = NoorElevatedSurface,
    onSecondaryContainer = NoorPurpleLight,

    tertiary = NoorCoral,
    onTertiary = NoorTextPrimary,
    tertiaryContainer = NoorCardBackground,
    onTertiaryContainer = NoorCoralLight,

    background = NoorPrimaryBackground,
    onBackground = NoorTextPrimary,

    surface = NoorSecondaryBackground,
    onSurface = NoorTextPrimary,
    surfaceVariant = NoorCardBackground,
    onSurfaceVariant = NoorTextSecondary,

    outline = NoorBorder,
    outlineVariant = NoorBorder,

    error = NoorError,
    onError = NoorTextPrimary
)

@Composable
fun NoorFlixTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = NoorFlixColorScheme,
        typography = Typography,
        content = content
    )
}
