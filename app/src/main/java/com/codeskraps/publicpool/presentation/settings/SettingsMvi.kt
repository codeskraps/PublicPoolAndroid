package com.codeskraps.publicpool.presentation.settings

import com.codeskraps.publicpool.presentation.common.UiEffect
import com.codeskraps.publicpool.presentation.common.UiEvent
import com.codeskraps.publicpool.presentation.common.UiState

// --- State ---
data class SettingsState(
    val walletAddress: String = "",
    val isLoading: Boolean = true // Start loading initially
) : UiState

// --- Events ---
sealed interface SettingsEvent : UiEvent {
    data class WalletAddressChanged(val address: String) : SettingsEvent
    data object SaveWalletAddress : SettingsEvent
    data object LoadWalletAddress : SettingsEvent // To trigger initial load
    data object OnScreenVisible : SettingsEvent
}

// --- Effects ---
sealed interface SettingsEffect : UiEffect {
    data object WalletAddressSaved : SettingsEffect
    data class ShowError(val message: String) : SettingsEffect
} 