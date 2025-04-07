package com.codeskraps.publicpool.domain.usecase

import com.codeskraps.publicpool.domain.repository.AnalyticsRepository

class IdentifyUserUseCase(
    private val analyticsRepository: AnalyticsRepository
) {
    suspend operator fun invoke(walletAddress: String?) {
        analyticsRepository.identifyUser(walletAddress)
    }
} 