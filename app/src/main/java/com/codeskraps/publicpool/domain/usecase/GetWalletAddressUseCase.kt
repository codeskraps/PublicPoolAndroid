package com.codeskraps.publicpool.domain.usecase

import com.codeskraps.publicpool.domain.repository.PublicPoolRepository
import kotlinx.coroutines.flow.Flow

class GetWalletAddressUseCase(private val repository: PublicPoolRepository) {
    operator fun invoke(): Flow<String?> {
        return repository.getWalletAddress()
    }
} 