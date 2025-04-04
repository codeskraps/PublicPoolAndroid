package com.codeskraps.publicpool.domain.model

data class NetworkInfo(
    val networkDifficulty: Double,
    val networkHashRate: Double,
    val blockHeight: Long,
    val blockWeight: Long // Added from DTO
) 