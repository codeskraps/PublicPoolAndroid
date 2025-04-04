package com.codeskraps.publicpool.domain.usecase

import com.codeskraps.publicpool.domain.repository.PublicPoolRepository

class SaveWalletAddressUseCase(private val repository: PublicPoolRepository) {
    suspend operator fun invoke(address: String) {
        // Add validation logic here if needed before saving
        if (address.isNotBlank()) { // Basic validation
            repository.saveWalletAddress(address.trim())
        }
    }
} 