package com.codeskraps.publicpool.domain.model

import java.time.OffsetDateTime

data class CryptoPrice(
    val symbol: String,
    val price: Double,
    val currency: String, // e.g., "USD"
    val lastUpdated: OffsetDateTime? // When the price was last updated
) 