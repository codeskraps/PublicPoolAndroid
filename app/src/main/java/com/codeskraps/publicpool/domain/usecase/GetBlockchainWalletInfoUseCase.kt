package com.codeskraps.publicpool.domain.usecase

import com.codeskraps.publicpool.domain.model.WalletInfo
import com.codeskraps.publicpool.domain.repository.PublicPoolRepository

class GetBlockchainWalletInfoUseCase(
    private val repository: PublicPoolRepository
) {
    suspend operator fun invoke(walletAddress: String): Result<WalletInfo> {
        if (walletAddress.isBlank()) {
            return Result.failure(IllegalArgumentException("Wallet address cannot be blank"))
        }
        return repository.getBlockchainWalletInfo(walletAddress)
    }
} 