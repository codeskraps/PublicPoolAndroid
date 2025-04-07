package com.codeskraps.publicpool.data.repository

import com.codeskraps.publicpool.data.remote.UmamiAnalyticsDataSource
import com.codeskraps.publicpool.domain.repository.AnalyticsRepository

class AnalyticsRepositoryImpl(
    private val analyticsDataSource: UmamiAnalyticsDataSource
) : AnalyticsRepository {
    
    override suspend fun initialize() {
        analyticsDataSource.initialize()
    }
    
    override suspend fun trackPageView(pageName: String) {
        analyticsDataSource.trackPageView(pageName)
    }
    
    override suspend fun trackEvent(eventName: String, eventData: Map<String, String>) {
        analyticsDataSource.trackEvent(eventName, eventData)
    }
    
    override suspend fun identifyUser(walletAddress: String?) {
        analyticsDataSource.identifyUser(walletAddress)
    }
} 