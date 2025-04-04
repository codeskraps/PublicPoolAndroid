package com.codeskraps.publicpool.data.remote.dto

import kotlinx.serialization.Serializable

// API returns a list directly, so we define the item structure
@Serializable
data class ChartDataPointDto(
    val label: String, // ISO 8601 date-time string
    val data: String // Hash rate as a string, needs conversion
)

// We can use a typealias if needed, but Ktor can handle List<ChartDataPointDto> directly
// typealias ChartDataDto = List<ChartDataPointDto> 