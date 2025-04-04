package com.codeskraps.publicpool.domain.usecase

import com.codeskraps.publicpool.domain.model.CryptoPrice
import com.codeskraps.publicpool.domain.repository.PublicPoolRepository

class GetBtcPriceUseCase(
    private val repository: PublicPoolRepository
) {
    // Default currency can be USD, or allow specifying
    suspend operator fun invoke(): Result<CryptoPrice> {
        return repository.getBtcPriceUsdt()
    }
} 