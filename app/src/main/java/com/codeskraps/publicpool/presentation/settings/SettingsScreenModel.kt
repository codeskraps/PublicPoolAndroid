package com.codeskraps.publicpool.presentation.settings

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.codeskraps.publicpool.domain.usecase.GetWalletAddressUseCase
import com.codeskraps.publicpool.domain.usecase.IdentifyUserUseCase
import com.codeskraps.publicpool.domain.usecase.SaveWalletAddressUseCase
import com.codeskraps.publicpool.domain.usecase.TrackPageViewUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SettingsScreenModel(
    private val getWalletAddressUseCase: GetWalletAddressUseCase,
    private val saveWalletAddressUseCase: SaveWalletAddressUseCase,
    private val identifyUserUseCase: IdentifyUserUseCase,
    private val trackPageViewUseCase: TrackPageViewUseCase
) : StateScreenModel<SettingsState>(SettingsState()) { // Initialize with default state

    private val _effect = Channel<SettingsEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        // Trigger loading the address when the ScreenModel is created
        handleEvent(SettingsEvent.LoadWalletAddress)
    }

    fun handleEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.WalletAddressChanged -> {
                // Update state directly for text field changes
                mutableState.update { it.copy(walletAddress = event.address) }
            }
            SettingsEvent.SaveWalletAddress -> saveWalletAddress()
            SettingsEvent.LoadWalletAddress -> loadWalletAddress()
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
                    // Handle error loading address
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
                    
                    // Identify user with the loaded wallet address
                    identifyUserUseCase(address)
                }
        }
    }

    private fun saveWalletAddress() {
        screenModelScope.launch {
            try {
                // Use the current address from the state, explicitly allowing blank addresses
                val addressToSave = mutableState.value.walletAddress
                saveWalletAddressUseCase(addressToSave)
                
                // Identify user with the newly saved wallet address
                identifyUserUseCase(addressToSave)
                
                _effect.send(SettingsEffect.WalletAddressSaved)
            } catch (e: Exception) {
                // Handle error saving address
                _effect.send(SettingsEffect.ShowError("Failed to save wallet address: ${e.message}"))
            }
        }
    }
} 