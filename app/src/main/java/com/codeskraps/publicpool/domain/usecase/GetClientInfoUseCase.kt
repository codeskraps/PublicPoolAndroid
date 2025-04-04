package com.codeskraps.publicpool.domain.usecase

import com.codeskraps.publicpool.domain.model.ClientInfo
import com.codeskraps.publicpool.domain.repository.PublicPoolRepository

class GetClientInfoUseCase(private val repository: PublicPoolRepository) {
    suspend operator fun invoke(walletAddress: String): Result<ClientInfo> {
        if (walletAddress.isBlank()) {
            return Result.failure(IllegalArgumentException("Wallet address cannot be blank"))
        }
        return repository.getClientInfo(walletAddress)
    }
} 