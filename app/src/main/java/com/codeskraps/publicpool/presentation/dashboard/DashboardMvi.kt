package com.codeskraps.publicpool.presentation.dashboard

import com.codeskraps.publicpool.domain.model.ChartDataPoint
import com.codeskraps.publicpool.domain.model.ClientInfo
import com.codeskraps.publicpool.domain.model.NetworkInfo
import com.codeskraps.publicpool.presentation.common.UiEffect
import com.codeskraps.publicpool.presentation.common.UiEvent
import com.codeskraps.publicpool.presentation.common.UiState

// --- State ---
data class DashboardState(
    // Wallet Address
    val walletAddress: String? = null,

    // Data States
    val networkInfo: NetworkInfo? = null,
    val clientInfo: ClientInfo? = null,
    val chartData: List<ChartDataPoint> = emptyList(),
    val chartDataTwoHourAvg: List<ChartDataPoint> = emptyList(),

    // Loading States
    val isWalletLoading: Boolean = true, // Loading wallet from DataStore
    val isNetworkLoading: Boolean = false,
    val isClientInfoLoading: Boolean = false,
    val isChartDataLoading: Boolean = false,

    // Error States (can be more granular if needed)
    val errorMessage: String? = null

) : UiState {
    // Combined loading state for overall screen indication (optional)
    val isLoading: Boolean
        get() = isWalletLoading || isNetworkLoading || isClientInfoLoading || isChartDataLoading
}

// --- Events ---
sealed interface DashboardEvent : UiEvent {
    data object LoadData : DashboardEvent // Initial load trigger
    data object RefreshData : DashboardEvent
    data object GoToSettings : DashboardEvent
    data class WalletAddressLoaded(val address: String?) : DashboardEvent // Internal event
    data class NetworkInfoResult(val result: Result<NetworkInfo>) : DashboardEvent // Internal event
    data class ClientInfoResult(val result: Result<ClientInfo>) : DashboardEvent // Internal event
    data class ChartDataResult(val result: Result<List<ChartDataPoint>>) : DashboardEvent // Internal event
}

// --- Effects ---
sealed interface DashboardEffect : UiEffect {
    data object NavigateToSettings : DashboardEffect
    data class ShowErrorSnackbar(val message: String) : DashboardEffect
} 