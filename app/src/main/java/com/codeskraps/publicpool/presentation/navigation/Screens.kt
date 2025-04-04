package com.codeskraps.publicpool.presentation.navigation

import android.os.Parcelable
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.koin.koinScreenModel // Import the new function
import com.codeskraps.publicpool.presentation.dashboard.DashboardScreenModel
import com.codeskraps.publicpool.presentation.dashboard.DashboardContent // We'll create this Composable
import com.codeskraps.publicpool.presentation.settings.SettingsScreenModel
import com.codeskraps.publicpool.presentation.settings.SettingsContent // We'll create this Composable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize // Required for Screen serialization if needed

// Using Parcelize allows Screens to be potentially passed in bundles, though not strictly necessary
// for basic navigation if you reconstruct them.

@Parcelize
data object DashboardScreen : Screen, Parcelable {
    // Optional: Define a unique key if needed for specific navigator operations
    @IgnoredOnParcel
    override val key: ScreenKey = uniqueScreenKey

    private fun readResolve(): Any = DashboardScreen

    @Composable
    override fun Content() {
        // Use koinScreenModel from voyager-koin
        val screenModel: DashboardScreenModel = koinScreenModel()
        DashboardContent(screenModel) // Pass ScreenModel to the actual UI content
    }
}

@Parcelize
data object SettingsScreen : Screen, Parcelable {
    @IgnoredOnParcel
    override val key: ScreenKey = uniqueScreenKey

    private fun readResolve(): Any = SettingsScreen

    @Composable
    override fun Content() {
        val screenModel: SettingsScreenModel = koinScreenModel()
        SettingsContent(screenModel)
    }
} 