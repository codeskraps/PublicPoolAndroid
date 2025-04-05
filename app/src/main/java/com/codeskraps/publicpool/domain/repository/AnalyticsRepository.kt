package com.codeskraps.publicpool.domain.repository

interface AnalyticsRepository {
    suspend fun initialize()
    suspend fun trackPageView(pageName: String)
    suspend fun trackEvent(eventName: String, eventData: Map<String, String> = emptyMap())
} 