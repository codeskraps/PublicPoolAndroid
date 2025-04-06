package com.codeskraps.publicpool

import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.codeskraps.publicpool.di.AppReadinessState
import com.codeskraps.publicpool.presentation.navigation.DashboardTab
import com.codeskraps.publicpool.presentation.navigation.WalletTab
import com.codeskraps.publicpool.presentation.navigation.WorkersTab
import com.codeskraps.publicpool.ui.theme.PublicPoolTheme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    val appReadinessState: AppReadinessState by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setupSplashScreenKeepCondition()

        setContent {
            PublicPoolTheme {
                // Use Navigator as the ROOT container for ALL screens (including settings)
                Navigator(HomeScreen()) {
                    // Show the current screen
                    CurrentScreen()
                }
            }
        }
    }

    private fun setupSplashScreenKeepCondition() {
        val content: View = findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    return if (appReadinessState.isReady.value) {
                        content.viewTreeObserver.removeOnPreDrawListener(this)
                        true
                    } else {
                        false
                    }
                }
            }
        )
    }
}

/**
 * Main home screen that contains the tabs
 */
class HomeScreen : Screen {
    @Composable
    override fun Content() {
        TabNavigator(DashboardTab) { tabNavigator ->
            Scaffold(
                bottomBar = {
                    Column {
                        HorizontalDivider(
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f)
                        )
                        NavigationBar {
                            TabNavigationItem(DashboardTab)
                            TabNavigationItem(WorkersTab)
                            TabNavigationItem(WalletTab)
                        }
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    // Show the current tab content
                    tabNavigator.current.Content()
                }
            }
        }
    }

    @Composable
    private fun RowScope.TabNavigationItem(tab: Tab) {
        val tabNavigator = LocalTabNavigator.current

        NavigationBarItem(
            selected = tabNavigator.current == tab,
            onClick = { tabNavigator.current = tab },
            icon = {
                tab.options.icon?.let {
                    Icon(painter = it, contentDescription = tab.options.title)
                }
            },
            label = { Text(text = tab.options.title) }
        )
    }
}