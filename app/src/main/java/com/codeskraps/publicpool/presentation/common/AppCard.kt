package com.codeskraps.publicpool.presentation.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Base card composable that enforces the app's surface color and border style.
 */
@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    shape: Shape = CardDefaults.shape,
    elevation: Dp = 1.dp, // Default elevation
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface // Ensure surface color is used
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        // Use the outline color from the theme for the border
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        content = content
    )
} 