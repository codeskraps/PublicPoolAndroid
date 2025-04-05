package com.codeskraps.publicpool.domain.usecase

import com.codeskraps.publicpool.domain.repository.AnalyticsRepository

class InitializeAnalyticsUseCase(
    private val analyticsRepository: AnalyticsRepository
) {
    suspend operator fun invoke() {
        analyticsRepository.initialize()
    }
} 