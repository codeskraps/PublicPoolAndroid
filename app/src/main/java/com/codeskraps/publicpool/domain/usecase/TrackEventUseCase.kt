package com.codeskraps.publicpool.domain.usecase

import com.codeskraps.publicpool.domain.repository.AnalyticsRepository

class TrackEventUseCase(
    private val analyticsRepository: AnalyticsRepository
) {
    suspend operator fun invoke(eventName: String, eventData: Map<String, String> = emptyMap()) {
        analyticsRepository.trackEvent(eventName, eventData)
    }
} 