package com.codeskraps.publicpool.data.mappers

import com.codeskraps.publicpool.data.remote.dto.BinanceTickerDto
import com.codeskraps.publicpool.data.remote.dto.ChartDataPointDto
import com.codeskraps.publicpool.data.remote.dto.ClientInfoDto
import com.codeskraps.publicpool.data.remote.dto.NetworkInfoDto
import com.codeskraps.publicpool.data.remote.dto.TransactionDto
import com.codeskraps.publicpool.data.remote.dto.WalletInfoDto
import com.codeskraps.publicpool.data.remote.dto.WorkerDto
import com.codeskraps.publicpool.domain.model.ChartDataPoint
import com.codeskraps.publicpool.domain.model.ClientInfo
import com.codeskraps.publicpool.domain.model.CryptoPrice
import com.codeskraps.publicpool.domain.model.NetworkInfo
import com.codeskraps.publicpool.domain.model.WalletInfo
import com.codeskraps.publicpool.domain.model.WalletTransaction
import com.codeskraps.publicpool.domain.model.Worker
import com.codeskraps.publicpool.domain.model.toOffsetDateTime
import kotlinx.serialization.json.Json
import java.time.OffsetDateTime
import java.time.format.DateTimeParseException

// Inject or provide Json instance used by Ktor
// For simplicity here, create a default one. Ensure it matches Ktor's config (ignoreUnknownKeys=true)
private val jsonParser = Json { ignoreUnknownKeys = true }

// --- DTO to Domain Mappers ---

fun NetworkInfoDto.toDomain(): NetworkInfo {
    return NetworkInfo(
        networkDifficulty = this.difficulty ?: 0.0,
        networkHashRate = this.networkHashPS ?: 0.0,
        blockHeight = this.blocks ?: 0L,
        blockWeight = this.currentBlockWeight ?: 0L
    )
}

fun WorkerDto.toDomain(): Worker {
    return Worker(
        id = this.name ?: "Unknown Worker",
        sessionId = this.sessionId,
        bestDifficulty = this.bestDifficulty?.toDoubleOrNull(),
        hashRate = this.hashRate?.toDoubleOrNull(),
        startTime = this.startTime,
        lastSeen = this.lastSeen
    )
}

fun ClientInfoDto.toDomain(): ClientInfo {
    return ClientInfo(
        // Handle potential conversion or formatting for difficulty later if needed
        bestDifficulty = this.bestDifficulty ?: "0",
        workersCount = this.workersCount ?: 0,
        workers = this.workers?.map { it.toDomain() } ?: emptyList()
    )
}

fun ChartDataPointDto.toDomain(): ChartDataPoint? { // Return nullable if parsing fails
    return try {
        ChartDataPoint(
            timestamp = OffsetDateTime.parse(this.label), // Parse ISO 8601 string
            hashRate = this.data.toDoubleOrNull() ?: 0.0 // Convert string data to Double
        )
    } catch (e: DateTimeParseException) {
        // Log error or handle invalid date format
        null
    } catch (e: NumberFormatException) {
        // Log error or handle invalid number format
        null
    }
}

// Helper to map a list, filtering out nulls from failed conversions
fun List<ChartDataPointDto>.toDomainList(): List<ChartDataPoint> {
    return this.mapNotNull { it.toDomain() }
}

// --- Mappers for blockchain.info ---

fun TransactionDto.toDomain(): WalletTransaction {
    return WalletTransaction(
        hash = this.hash,
        time = this.time.toOffsetDateTime(), // Convert Unix timestamp
        resultSatoshis = this.result ?: 0L,
        feeSatoshis = this.fee ?: 0L
    )
}

fun WalletInfoDto.toDomain(): WalletInfo {
    return WalletInfo(
        address = this.address,
        finalBalanceSatoshis = this.finalBalance ?: 0L,
        totalReceivedSatoshis = this.totalReceived ?: 0L,
        totalSentSatoshis = this.totalSent ?: 0L,
        transactionCount = this.nTx ?: 0L,
        transactions = this.txs?.map { it.toDomain() } ?: emptyList()
    )
}

// --- Mapper for Binance Ticker ---

fun BinanceTickerDto.toCryptoPrice(currency: String = "USD"): CryptoPrice? {
    return try {
        val priceValue = this.price.toDoubleOrNull() ?: return null

        // Extract base symbol (e.g., BTC from BTCUSDT)
        val baseSymbol = this.symbol.removeSuffix(currency)

        CryptoPrice(
            symbol = baseSymbol,
            price = priceValue,
            currency = currency,
            lastUpdated = OffsetDateTime.now() // Binance API doesn't provide timestamp here
        )
    } catch (e: Exception) {
        // Log error
        null
    }
} 