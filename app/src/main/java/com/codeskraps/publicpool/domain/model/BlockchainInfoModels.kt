package com.codeskraps.publicpool.domain.model

import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId

// Represents overall wallet summary
data class WalletInfo(
    val address: String,
    val finalBalanceSatoshis: Long,
    val totalReceivedSatoshis: Long,
    val totalSentSatoshis: Long,
    val transactionCount: Long,
    val transactions: List<WalletTransaction> // Include simplified transaction list
) {
    // Helper to convert satoshis to BTC
    val finalBalanceBtc: Double
        get() = finalBalanceSatoshis / 100_000_000.0
    val totalReceivedBtc: Double
        get() = totalReceivedSatoshis / 100_000_000.0
    val totalSentBtc: Double
        get() = totalSentSatoshis / 100_000_000.0
}

// Represents a single simplified transaction for display
data class WalletTransaction(
    val hash: String,
    val time: OffsetDateTime?, // Parsed time
    val resultSatoshis: Long, // Net change for this address
    val feeSatoshis: Long
) {
    val resultBtc: Double
        get() = resultSatoshis / 100_000_000.0
}

// Helper function to convert Unix timestamp (seconds) to OffsetDateTime
fun Long?.toOffsetDateTime(): OffsetDateTime? {
    if (this == null) return null
    return try {
        OffsetDateTime.ofInstant(Instant.ofEpochSecond(this), ZoneId.systemDefault())
    } catch (e: Exception) {
        null // Handle potential parsing errors
    }
} 