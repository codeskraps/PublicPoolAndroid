package com.codeskraps.publicpool.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

// The defined dark color scheme using custom colors
private val PublicPoolColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    background = AppBackground,
    surface = AppSurface, // Used by TopAppBar, Card, NavigationBar
    surfaceContainer = AppSurface, // Explicitly set for components like Nav Bar
    onBackground = AppOnSurface,
    onSurface = AppOnSurface,
    onSurfaceVariant = AppOnSurfaceVariant,
    error = DarkError,
    onError = DarkOnError,
    outline = CardBorder // Use the border color for outlines
)

@Composable
fun PublicPoolTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = PublicPoolColorScheme,
        typography = Typography,
        content = content
    )
}