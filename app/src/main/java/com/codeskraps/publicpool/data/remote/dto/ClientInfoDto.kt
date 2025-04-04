package com.codeskraps.publicpool.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ClientInfoDto(
    val bestDifficulty: String?, // Can be null if no data yet? API seems to send "0" sometimes
    val workersCount: Int?,
    val workers: List<WorkerDto>? // Define WorkerDto if needed, empty in example
)