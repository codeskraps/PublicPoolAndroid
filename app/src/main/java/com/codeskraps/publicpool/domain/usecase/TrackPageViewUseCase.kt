package com.codeskraps.publicpool.domain.usecase

import com.codeskraps.publicpool.domain.repository.AnalyticsRepository

class TrackPageViewUseCase(
    private val analyticsRepository: AnalyticsRepository
) {
    suspend operator fun invoke(pageName: String) {
        analyticsRepository.trackPageView(pageName)
    }
} 