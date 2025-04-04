package com.codeskraps.publicpool.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class WorkerDto(
    val sessionId: String? = null,
    val name: String? = null,
    val bestDifficulty: String? = null, // Difficulty as String
    val hashRate: String? = null, // Hash rate as String
    val startTime: String? = null, // ISO 8601 date-time string
    val lastSeen: String? = null // ISO 8601 date-time string
) 