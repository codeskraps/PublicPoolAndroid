package com.codeskraps.publicpool.presentation.navigation

import android.os.Parcelable
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import com.codeskraps.publicpool.presentation.dashboard.DashboardContent
import com.codeskraps.publicpool.presentation.dashboard.DashboardScreenModel
import com.codeskraps.publicpool.presentation.settings.SettingsContent
import com.codeskraps.publicpool.presentation.settings.SettingsScreenModel
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

/**
 * DashboardScreen - Renders the dashboard content with its screen model
 */
@Parcelize
data object DashboardScreen : Screen, Parcelable {
    @IgnoredOnParcel
    override val key: ScreenKey = uniqueScreenKey

    private fun readResolve(): Any = DashboardScreen

    @Composable
    override fun Content() {
        val screenModel: DashboardScreenModel = koinScreenModel()
        DashboardContent(screenModel)
    }
}

/**
 * SettingsScreen - Renders the settings content with its screen model
 */
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