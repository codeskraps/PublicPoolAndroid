package com.codeskraps.publicpool.presentation.workers

import com.codeskraps.publicpool.domain.model.Worker
import com.codeskraps.publicpool.presentation.common.UiEffect
import com.codeskraps.publicpool.presentation.common.UiEvent
import com.codeskraps.publicpool.presentation.common.UiState

// --- State ---
data class WorkersState(
    // val workers: List<Worker> = emptyList(), // Removed redundant list
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val walletAddress: String? = null, // To know if we should fetch
    val isWalletLoading: Boolean = true,
    val groupedWorkers: Map<String, List<Worker>> = emptyMap() // Keep the grouped map used by UI
) : UiState

// --- Events ---
sealed interface WorkersEvent : UiEvent {
    data object LoadWorkers : WorkersEvent
    data class WalletAddressLoaded(val address: String?) : WorkersEvent // Internal
    data object OnScreenVisible : WorkersEvent
}

// --- Effects ---
sealed interface WorkersEffect : UiEffect {
    // No specific effects needed for now, maybe error snackbars?
    data class ShowError(val message: String) : WorkersEffect
} 