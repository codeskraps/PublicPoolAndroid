package com.codeskraps.publicpool.data.remote.dto

import kotlinx.serialization.Serializable

// --- DTO for Binance Ticker Price ---

@Serializable
data class BinanceTickerDto(
    val symbol: String, // e.g., "BTCUSDT"
    val price: String // Price is returned as a String
) 