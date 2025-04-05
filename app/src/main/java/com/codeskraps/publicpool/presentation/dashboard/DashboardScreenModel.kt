package com.codeskraps.publicpool.presentation.dashboard

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.codeskraps.publicpool.domain.model.ChartDataPoint
import com.codeskraps.publicpool.domain.usecase.CalculateTwoHourAverageUseCase
import com.codeskraps.publicpool.domain.usecase.GetChartDataUseCase
import com.codeskraps.publicpool.domain.usecase.GetClientInfoUseCase
import com.codeskraps.publicpool.domain.usecase.GetNetworkInfoUseCase
import com.codeskraps.publicpool.domain.usecase.GetWalletAddressUseCase
import com.codeskraps.publicpool.domain.usecase.TrackPageViewUseCase
import com.codeskraps.publicpool.domain.usecase.TrackEventUseCase
import com.codeskraps.publicpool.di.AppReadinessState
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DashboardScreenModel(
    private val getWalletAddressUseCase: GetWalletAddressUseCase,
    private val getNetworkInfoUseCase: GetNetworkInfoUseCase,
    private val getClientInfoUseCase: GetClientInfoUseCase,
    private val getChartDataUseCase: GetChartDataUseCase,
    private val calculateTwoHourAverageUseCase: CalculateTwoHourAverageUseCase,
    private val trackPageViewUseCase: TrackPageViewUseCase,
    private val trackEventUseCase: TrackEventUseCase,
    private val appReadinessState: AppReadinessState
) : StateScreenModel<DashboardState>(DashboardState()) {

    private val _effect = Channel<DashboardEffect>()
    val effect = _effect.receiveAsFlow()

    private var dataLoadingJob: Job? = null

    init {
        // Track page view
        screenModelScope.launch {
            trackPageViewUseCase("Dashboard")
        }
        
        // Start loading data immediately
        handleEvent(DashboardEvent.LoadData)
    }

    fun handleEvent(event: DashboardEvent) {
        when (event) {
            DashboardEvent.LoadData -> loadInitialData()
            DashboardEvent.RefreshData -> {
                refreshData()
                // Track refresh event
                screenModelScope.launch {
                    trackEventUseCase("dashboard_refresh", mapOf("action" to "pull_to_refresh"))
                }
            }
            DashboardEvent.GoToSettings -> {
                sendEffect(DashboardEffect.NavigateToSettings)
                // Track settings navigation
                screenModelScope.launch {
                    trackEventUseCase("navigation", mapOf("to" to "settings", "from" to "dashboard"))
                }
            }
            // Internal Events triggered by data loading flows/calls
            is DashboardEvent.WalletAddressLoaded -> processWalletAddress(event.address)
            is DashboardEvent.NetworkInfoResult -> processNetworkInfoResult(event.result)
            is DashboardEvent.ClientInfoResult -> processClientInfoResult(event.result)
            is DashboardEvent.ChartDataResult -> processChartDataResult(event.result)
        }
    }

    private fun loadInitialData() {
        // Collect wallet address changes
        screenModelScope.launch {
            getWalletAddressUseCase()
                .onStart { mutableState.update { it.copy(isWalletLoading = true) } }
                .catch { e ->
                    mutableState.update { it.copy(isWalletLoading = false, errorMessage = "Failed to load wallet address") }
                    sendEffect(DashboardEffect.ShowErrorSnackbar("Error loading wallet: ${e.message}"))
                }
                .collect { address ->
                    handleEvent(DashboardEvent.WalletAddressLoaded(address))
                }
        }

        // Fetch Network Info (doesn't depend on wallet)
        fetchNetworkInfo()
    }

    private fun processWalletAddress(address: String?) {
        appReadinessState.setReady()

        mutableState.update { it.copy(walletAddress = address, isWalletLoading = false) }
        if (address != null && address.isNotBlank()) {
            // Wallet address available, fetch client-specific data
            fetchClientInfoAndChartData(address)
        } else {
            // No wallet address, clear client data and show appropriate message/state
            mutableState.update {
                it.copy(
                    clientInfo = null,
                    chartData = emptyList(),
                    chartDataTwoHourAvg = emptyList(),
                    isClientInfoLoading = false,
                    isChartDataLoading = false,
                    errorMessage = if (!it.isWalletLoading) "Please set a wallet address in Settings" else it.errorMessage
                )
            }
            // Cancel any ongoing client/chart data fetching if wallet becomes null/blank
            dataLoadingJob?.cancel()
            dataLoadingJob = null
        }
    }

    private fun refreshData() {
        dataLoadingJob?.cancel() // Cancel previous jobs if any
        mutableState.update { it.copy(errorMessage = null) } // Clear previous errors
        fetchNetworkInfo()
        state.value.walletAddress?.let { address ->
            if (address.isNotBlank()) {
                fetchClientInfoAndChartData(address, isRefresh = true)
            }
        }
    }

    private fun fetchNetworkInfo() {
        screenModelScope.launch {
            mutableState.update { it.copy(isNetworkLoading = true) }
            val result = getNetworkInfoUseCase()
            handleEvent(DashboardEvent.NetworkInfoResult(result))
        }
    }

    private fun fetchClientInfoAndChartData(address: String, isRefresh: Boolean = false) {
        dataLoadingJob?.cancel() // Cancel previous loads before starting new ones
        dataLoadingJob = screenModelScope.launch {
            mutableState.update {
                it.copy(
                    isClientInfoLoading = true,
                    isChartDataLoading = true,
                    // Clear previous data when refreshing with a new address
                    clientInfo = null,
                    chartData = emptyList(),
                    chartDataTwoHourAvg = emptyList()
                )
            }

            // Launch both fetches concurrently
            launch {
                val clientInfoResult = getClientInfoUseCase(address)
                handleEvent(DashboardEvent.ClientInfoResult(clientInfoResult))
            }
            launch {
                val chartDataResult = getChartDataUseCase(address)
                handleEvent(DashboardEvent.ChartDataResult(chartDataResult))
            }
        }
    }

    private fun processNetworkInfoResult(result: Result<com.codeskraps.publicpool.domain.model.NetworkInfo>) {
        result.onSuccess {
            mutableState.update { s -> s.copy(networkInfo = it, isNetworkLoading = false) }
        }.onFailure {
            mutableState.update { s -> s.copy(isNetworkLoading = false, errorMessage = "Failed to load network info") }
            sendEffect(DashboardEffect.ShowErrorSnackbar("Network Error: ${it.message}"))
        }
    }

    private fun processClientInfoResult(result: Result<com.codeskraps.publicpool.domain.model.ClientInfo>) {
        result.onSuccess {
            mutableState.update { s -> s.copy(clientInfo = it, isClientInfoLoading = false) }
        }.onFailure {
            mutableState.update { s -> s.copy(isClientInfoLoading = false, errorMessage = "Failed to load client info") }
            sendEffect(DashboardEffect.ShowErrorSnackbar("Client Info Error: ${it.message}"))
        }
    }

    private fun processChartDataResult(result: Result<List<ChartDataPoint>>) {
        result.onSuccess {
            // Calculate 2-hour average from the fetched 10-min data
            val twoHourAvg = calculateTwoHourAverageUseCase(it)
            mutableState.update { s ->
                s.copy(
                    chartData = it,
                    chartDataTwoHourAvg = twoHourAvg,
                    isChartDataLoading = false
                )
            }
        }.onFailure {
            mutableState.update { s -> s.copy(isChartDataLoading = false, errorMessage = "Failed to load chart data") }
            sendEffect(DashboardEffect.ShowErrorSnackbar("Chart Data Error: ${it.message}"))
        }
    }

    private fun sendEffect(effectToSend: DashboardEffect) {
        screenModelScope.launch {
            _effect.send(effectToSend)
        }
    }
} 