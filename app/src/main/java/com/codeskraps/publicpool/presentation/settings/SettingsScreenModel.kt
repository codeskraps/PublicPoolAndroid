package com.codeskraps.publicpool.presentation.settings

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.codeskraps.publicpool.domain.usecase.GetWalletAddressUseCase
import com.codeskraps.publicpool.domain.usecase.IdentifyUserUseCase
import com.codeskraps.publicpool.domain.usecase.SaveWalletAddressUseCase
import com.codeskraps.publicpool.domain.usecase.TrackPageViewUseCase
import com.codeskraps.publicpool.domain.usecase.GetBaseUrlUseCase
import com.codeskraps.publicpool.domain.usecase.SaveBaseUrlUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SettingsScreenModel(
    private val getWalletAddressUseCase: GetWalletAddressUseCase,
    private val saveWalletAddressUseCase: SaveWalletAddressUseCase,
    private val getBaseUrlUseCase: GetBaseUrlUseCase,
    private val saveBaseUrlUseCase: SaveBaseUrlUseCase,
    private val identifyUserUseCase: IdentifyUserUseCase,
    private val trackPageViewUseCase: TrackPageViewUseCase
) : StateScreenModel<SettingsState>(SettingsState()) {

    private val _effect = Channel<SettingsEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        // Trigger loading when the ScreenModel is created
        handleEvent(SettingsEvent.LoadWalletAddress)
        handleEvent(SettingsEvent.LoadBaseUrl)
    }

    fun handleEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.WalletAddressChanged -> {
                mutableState.update { it.copy(walletAddress = event.address) }
            }
            is SettingsEvent.BaseUrlChanged -> {
                mutableState.update { it.copy(baseUrl = event.url) }
            }
            SettingsEvent.SaveSettings -> saveSettings()
            SettingsEvent.LoadWalletAddress -> loadWalletAddress()
            SettingsEvent.LoadBaseUrl -> loadBaseUrl()
            SettingsEvent.OnScreenVisible -> {
                screenModelScope.launch {
                    trackPageViewUseCase("Settings")
                }
            }
        }
    }

    private fun loadWalletAddress() {
        screenModelScope.launch {
            getWalletAddressUseCase()
                .onStart { mutableState.update { it.copy(isLoading = true) } }
                .catch { e ->
                    mutableState.update { it.copy(isLoading = false) }
                    _effect.send(SettingsEffect.ShowError("Failed to load wallet address: ${e.message}"))
                }
                .collect { address ->
                    mutableState.update {
                        it.copy(
                            walletAddress = address ?: "",
                            isLoading = false
                        )
                    }
                    
                    identifyUserUseCase(address)
                }
        }
    }

    private fun loadBaseUrl() {
        screenModelScope.launch {
            getBaseUrlUseCase()
                .catch { e ->
                    _effect.send(SettingsEffect.ShowError("Failed to load base URL: ${e.message}"))
                }
                .collect { url ->
                    mutableState.update { it.copy(baseUrl = url) }
                }
        }
    }

    private fun saveSettings() {
        screenModelScope.launch {
            try {
                // Save both wallet address and base URL
                val currentState = mutableState.value
                saveWalletAddressUseCase(currentState.walletAddress)
                saveBaseUrlUseCase(currentState.baseUrl)
                
                // Identify user with the new wallet address
                identifyUserUseCase(currentState.walletAddress)
                
                // Send both success effects
                _effect.send(SettingsEffect.WalletAddressSaved)
                _effect.send(SettingsEffect.BaseUrlSaved)
            } catch (e: Exception) {
                _effect.send(SettingsEffect.ShowError("Failed to save settings: ${e.message}"))
            }
        }
    }
} 