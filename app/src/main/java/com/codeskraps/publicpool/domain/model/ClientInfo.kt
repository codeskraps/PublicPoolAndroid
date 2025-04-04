package com.codeskraps.publicpool.domain.model

data class ClientInfo(
    val bestDifficulty: String, // Keep as String for display formatting? Or convert?
    val workersCount: Int,
    val workers: List<Worker> // Assuming Worker is a data class defined elsewhere
) 