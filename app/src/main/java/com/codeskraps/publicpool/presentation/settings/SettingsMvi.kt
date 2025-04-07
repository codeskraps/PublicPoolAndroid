package com.codeskraps.publicpool.presentation.settings

import com.codeskraps.publicpool.presentation.common.UiEffect
import com.codeskraps.publicpool.presentation.common.UiEvent
import com.codeskraps.publicpool.presentation.common.UiState

// --- State ---
data class SettingsState(
    val walletAddress: String = "",
    val baseUrl: String = "https://public-pool.io:40557/api",
    val isLoading: Boolean = true // Start loading initially
) : UiState

// --- Events ---
sealed interface SettingsEvent : UiEvent {
    data class WalletAddressChanged(val address: String) : SettingsEvent
    data class BaseUrlChanged(val url: String) : SettingsEvent
    data object SaveSettings : SettingsEvent
    data object LoadWalletAddress : SettingsEvent
    data object LoadBaseUrl : SettingsEvent
    data object OnScreenVisible : SettingsEvent
}

// --- Effects ---
sealed interface SettingsEffect : UiEffect {
    data object WalletAddressSaved : SettingsEffect
    data object BaseUrlSaved : SettingsEffect
    data class ShowError(val message: String) : SettingsEffect
} 