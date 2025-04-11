package com.codeskraps.publicpool.presentation.workers

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.codeskraps.publicpool.domain.usecase.GetClientInfoUseCase
import com.codeskraps.publicpool.domain.usecase.GetWalletAddressUseCase
import com.codeskraps.publicpool.domain.usecase.TrackEventUseCase
import com.codeskraps.publicpool.domain.usecase.TrackPageViewUseCase
import com.codeskraps.publicpool.di.AppLifecycleState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WorkersScreenModel(
    private val getWalletAddressUseCase: GetWalletAddressUseCase,
    private val getClientInfoUseCase: GetClientInfoUseCase,
    private val trackPageViewUseCase: TrackPageViewUseCase,
    private val trackEventUseCase: TrackEventUseCase,
    private val appLifecycleState: AppLifecycleState
) : StateScreenModel<WorkersState>(WorkersState()) {

    private val _effect = Channel<WorkersEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        // Start loading wallet address immediately
        handleEvent(WorkersEvent.LoadWorkers)

        // Observe app lifecycle state
        screenModelScope.launch {
            appLifecycleState.isAppInBackground.collect { isInBackground ->
                if (!isInBackground) {
                    // App came back to foreground, trigger refresh
                    handleEvent(WorkersEvent.LoadWorkers)
                }
            }
        }
    }

    fun handleEvent(event: WorkersEvent) {
        when (event) {
            WorkersEvent.LoadWorkers -> {
                loadWalletAndWorkers()
                // Track refresh event when explicitly requested (not on initial load)
                if (state.value.walletAddress != null) {
                    screenModelScope.launch {
                        trackEventUseCase("workers_refresh", mapOf("action" to "pull_to_refresh"))
                    }
                }
            }
            WorkersEvent.OnScreenVisible -> {
                screenModelScope.launch {
                    trackPageViewUseCase("Workers")
                }
            }
            is WorkersEvent.WalletAddressLoaded -> processWalletAddress(event.address)
        }
    }

    private fun loadWalletAndWorkers() {
        screenModelScope.launch {
            getWalletAddressUseCase()
                .onStart { mutableState.update { it.copy(isWalletLoading = true) } }
                .catch { e ->
                    mutableState.update { it.copy(isWalletLoading = false, errorMessage = "Failed to load wallet address") }
                    sendEffect(WorkersEffect.ShowError("Error loading wallet: ${e.message}"))
                }
                .collect { address ->
                    handleEvent(WorkersEvent.WalletAddressLoaded(address))
                }
        }
    }

    private fun processWalletAddress(address: String?) {
        mutableState.update { it.copy(walletAddress = address, isWalletLoading = false) }
        if (!address.isNullOrBlank()) {
            fetchWorkers(address)
        } else {
            // No wallet address, clear grouped workers and ensure loading is false
            mutableState.update { it.copy(
                groupedWorkers = emptyMap(), // Clear the grouped workers map
                isLoading = false // Ensure isLoading is false
                // Optionally reset error message: errorMessage = null
                )
            }
        }
    }

    private fun fetchWorkers(address: String) {
        screenModelScope.launch {
            mutableState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = getClientInfoUseCase(address)
            result.onSuccess { clientInfo -> // Get the full ClientInfo
                // Group workers by name (Worker.id)
                val grouped = clientInfo.workers.groupBy { it.id }
                mutableState.update { s ->
                    s.copy(
                        groupedWorkers = grouped,
                        isLoading = false
                    )
                }
            }.onFailure {
                mutableState.update { s ->
                    s.copy(isLoading = false, errorMessage = "Failed to load workers")
                }
                sendEffect(WorkersEffect.ShowError("Worker loading error: ${it.message}"))
            }
        }
    }

    private fun sendEffect(effectToSend: WorkersEffect) {
        screenModelScope.launch {
            _effect.send(effectToSend)
        }
    }

    // Method to track worker group expansion/collapse
    suspend fun trackWorkerGroupToggle(workerName: String, isExpanded: Boolean) {
        val action = if (isExpanded) "expand" else "collapse"
        trackEventUseCase(
            "worker_group_toggle", 
            mapOf(
                "worker_name" to workerName,
                "action" to action
            )
        )
    }
} 