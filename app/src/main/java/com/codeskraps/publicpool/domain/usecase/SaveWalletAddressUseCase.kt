package com.codeskraps.publicpool.domain.usecase

import com.codeskraps.publicpool.domain.repository.PublicPoolRepository

class SaveWalletAddressUseCase(private val repository: PublicPoolRepository) {
    suspend operator fun invoke(address: String) {
        // Always save the address, including empty strings
        repository.saveWalletAddress(address.trim())
    }
} 