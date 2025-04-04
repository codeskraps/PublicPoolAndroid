package com.codeskraps.publicpool.domain.model

import java.time.OffsetDateTime // Use java.time for better date handling

data class ChartDataPoint(
    val timestamp: OffsetDateTime, // Parsed timestamp
    val hashRate: Double // Converted hash rate
) 