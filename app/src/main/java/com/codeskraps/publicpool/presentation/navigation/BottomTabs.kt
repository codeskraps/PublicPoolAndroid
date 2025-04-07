package com.codeskraps.publicpool.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.codeskraps.publicpool.R
import com.codeskraps.publicpool.presentation.wallet.WalletContent
import com.codeskraps.publicpool.presentation.workers.WorkersScreen

internal data object DashboardTab : Tab {
    private fun readResolve(): Any = DashboardTab
    override val options: TabOptions
        @Composable
        get() {
            val title = stringResource(id = R.string.tab_title_dashboard)
            val icon = rememberVectorPainter(Icons.Default.Home)
            return remember { 
                TabOptions(
                    index = 0u, 
                    title = title, 
                    icon = icon
                ) 
            }
        }

    @Composable
    override fun Content() {
        DashboardScreen.Content()
    }
}

internal data object WorkersTab : Tab {
    private fun readResolve(): Any = WorkersTab
    override val options: TabOptions
        @Composable
        get() {
            val title = stringResource(id = R.string.tab_title_workers)
            val icon = painterResource(id = R.drawable.device_hub)
            return remember { 
                TabOptions(
                    index = 1u, 
                    title = title, 
                    icon = icon
                ) 
            }
        }

    @Composable
    override fun Content() {
        WorkersScreen.Content()
    }
}

internal data object WalletTab : Tab {
    private fun readResolve(): Any = WalletTab
    override val options: TabOptions
        @Composable
        get() {
            val title = stringResource(id = R.string.tab_title_wallet)
            val icon = painterResource(id = R.drawable.wallet)
            return remember { 
                TabOptions(
                    index = 2u, 
                    title = title, 
                    icon = icon
                ) 
            }
        }

    @Composable
    override fun Content() {
        WalletContent.Content()
    }
} 