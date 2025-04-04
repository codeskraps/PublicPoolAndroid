package com.codeskraps.publicpool

import android.annotation.SuppressLint
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
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import cafe.adriel.voyager.navigator.CurrentScreen
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

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setupSplashScreenKeepCondition()

        setContent {
            PublicPoolTheme {
                TabNavigator(DashboardTab) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Box(modifier = Modifier.weight(1f)) {
                            CurrentScreen()
                        }
                        NavigationBar {
                            TabNavigationItem(tab = DashboardTab)
                            TabNavigationItem(tab = WorkersTab)
                            TabNavigationItem(tab = WalletTab)
                        }
                    }
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