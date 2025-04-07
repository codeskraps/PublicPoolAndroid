package com.codeskraps.publicpool.presentation.wallet

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.codeskraps.publicpool.domain.usecase.GetBlockchainWalletInfoUseCase
import com.codeskraps.publicpool.domain.usecase.GetWalletAddressUseCase
import com.codeskraps.publicpool.domain.usecase.GetBtcPriceUseCase
import com.codeskraps.publicpool.domain.usecase.TrackPageViewUseCase
import com.codeskraps.publicpool.domain.usecase.TrackEventUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import android.util.Log

class WalletScreenModel(
    private val getWalletAddressUseCase: GetWalletAddressUseCase,
    private val getBlockchainWalletInfoUseCase: GetBlockchainWalletInfoUseCase,
    private val getBtcPriceUseCase: GetBtcPriceUseCase,
    private val trackPageViewUseCase: TrackPageViewUseCase,
    private val trackEventUseCase: TrackEventUseCase
) : StateScreenModel<WalletState>(WalletState()) {

    private val _effect = Channel<WalletEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        // Start loading wallet address immediately
        handleEvent(WalletEvent.LoadWalletDetails)
    }

    fun handleEvent(event: WalletEvent) {
        when (event) {
            WalletEvent.LoadWalletDetails -> {
                loadWalletAndDetails()
                // Track refresh event when explicitly requested (not on initial load)
                if (state.value.walletAddress != null) {
                    screenModelScope.launch {
                        trackEventUseCase("wallet_refresh", mapOf("action" to "pull_to_refresh"))
                    }
                }
            }
            WalletEvent.OnScreenVisible -> {
                screenModelScope.launch {
                    trackPageViewUseCase("Wallet")
                }
            }
            is WalletEvent.WalletAddressLoaded -> processWalletAddress(event.address)
            is WalletEvent.PriceResult -> processPriceResult(event.result)
        }
    }

    private fun loadWalletAndDetails() {
        // Fetch BTC Price (doesn't depend on wallet address)
        fetchBtcPrice()

        // Fetch wallet address and details
        screenModelScope.launch {
            getWalletAddressUseCase()
                .onStart { mutableState.update { it.copy(isWalletLoading = true) } }
                .catch { e ->
                    mutableState.update { it.copy(isWalletLoading = false, errorMessage = "Failed to load wallet address") }
                    sendEffect(WalletEffect.ShowError("Error loading wallet: ${e.message}"))
                }
                .collect { address ->
                    handleEvent(WalletEvent.WalletAddressLoaded(address))
                }
        }
    }

    private fun processWalletAddress(address: String?) {
        mutableState.update { it.copy(walletAddress = address, isWalletLoading = false) }
        if (!address.isNullOrBlank()) {
            fetchWalletDetails(address)
        } else {
            // No wallet address, clear info and show message
            mutableState.update { it.copy(walletInfo = null, isLoading = false) }
        }
    }

    private fun fetchWalletDetails(address: String) {
        screenModelScope.launch {
            mutableState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = getBlockchainWalletInfoUseCase(address)
            result.onSuccess {
                mutableState.update { s ->
                    s.copy(
                        walletInfo = it, // Store fetched WalletInfo
                        isLoading = false
                    )
                }
            }.onFailure {
                mutableState.update { s ->
                    s.copy(isLoading = false, errorMessage = "Failed to load wallet details")
                }
                sendEffect(WalletEffect.ShowError("Wallet details error: ${it.message}"))
            }
        }
    }

    private fun fetchBtcPrice() {
        screenModelScope.launch {
            mutableState.update { it.copy(isPriceLoading = true) }
            val result = getBtcPriceUseCase()
            handleEvent(WalletEvent.PriceResult(result))
        }
    }

    private fun processPriceResult(result: Result<com.codeskraps.publicpool.domain.model.CryptoPrice>) {
        result.onSuccess {
            mutableState.update { s -> s.copy(btcPrice = it, isPriceLoading = false) }
        }.onFailure {
            mutableState.update { s -> s.copy(isPriceLoading = false) }
            Log.e("WalletScreenModel", "Failed to load BTC price", it)
        }
    }

    private fun sendEffect(effectToSend: WalletEffect) {
        screenModelScope.launch {
            _effect.send(effectToSend)
        }
    }
} 