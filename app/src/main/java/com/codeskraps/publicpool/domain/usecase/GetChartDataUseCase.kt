package com.codeskraps.publicpool.domain.usecase

import com.codeskraps.publicpool.domain.model.ChartDataPoint
import com.codeskraps.publicpool.domain.repository.PublicPoolRepository

class GetChartDataUseCase(private val repository: PublicPoolRepository) {
    suspend operator fun invoke(walletAddress: String): Result<List<ChartDataPoint>> {
        if (walletAddress.isBlank()) {
            return Result.failure(IllegalArgumentException("Wallet address cannot be blank"))
        }
        return repository.getChartData(walletAddress)
    }
} 