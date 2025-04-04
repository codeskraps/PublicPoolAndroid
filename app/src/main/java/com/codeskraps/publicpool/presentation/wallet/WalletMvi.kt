package com.codeskraps.publicpool.presentation.wallet

import com.codeskraps.publicpool.domain.model.WalletInfo
import com.codeskraps.publicpool.domain.model.CryptoPrice
import com.codeskraps.publicpool.presentation.common.UiEffect
import com.codeskraps.publicpool.presentation.common.UiEvent
import com.codeskraps.publicpool.presentation.common.UiState

// --- State ---
data class WalletState(
    val walletInfo: WalletInfo? = null,
    val btcPrice: CryptoPrice? = null,
    val isLoading: Boolean = false,
    val isPriceLoading: Boolean = false,
    val errorMessage: String? = null,
    val walletAddress: String? = null, // To know which address to query
    val isWalletLoading: Boolean = true // Loading address from DataStore
) : UiState {
    val isOverallLoading: Boolean
        get() = isLoading || isWalletLoading || isPriceLoading
}

// --- Events ---
sealed interface WalletEvent : UiEvent {
    data object LoadWalletDetails : WalletEvent
    data class WalletAddressLoaded(val address: String?) : WalletEvent // Internal
    data class PriceResult(val result: Result<CryptoPrice>) : WalletEvent // Internal for price
}

// --- Effects ---
sealed interface WalletEffect : UiEffect {
    data class ShowError(val message: String) : WalletEffect
} 