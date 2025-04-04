package com.codeskraps.publicpool.ui.theme

import android.app.Activity
import android.os.Build
// import androidx.compose.foundation.isSystemInDarkTheme // No longer needed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
// Remove dynamic/light color scheme imports if not used at all
// import androidx.compose.material3.dynamicDarkColorScheme
// import androidx.compose.material3.dynamicLightColorScheme
// import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

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

// Remove LightColorScheme if not needed
/*
private val LightColorScheme = lightColorScheme(...)
*/

@Composable
fun PublicPoolTheme(
    // Remove darkTheme and dynamicColor parameters
    content: @Composable () -> Unit
) {
    // Always use the custom dark color scheme
    val colorScheme = PublicPoolColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
            // Set navigation bar color if desired
            // window.navigationBarColor = colorScheme.surface.toArgb()

            // Always set status bar icons to light (because background is dark)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            // Always set navigation bar icons to light if nav bar color is dark
            // WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}