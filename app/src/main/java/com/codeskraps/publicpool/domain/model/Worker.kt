package com.codeskraps.publicpool.domain.model

data class Worker(
    val id: String, // Using 'name' as the unique ID
    val sessionId: String?,
    val bestDifficulty: Double?,
    val hashRate: Double?,
    val startTime: String?,
    val lastSeen: String?
) 