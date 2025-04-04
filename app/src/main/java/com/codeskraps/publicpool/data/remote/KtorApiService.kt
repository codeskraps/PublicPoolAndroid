package com.codeskraps.publicpool.data.remote

import com.codeskraps.publicpool.data.remote.dto.ChartDataPointDto
// No longer need ClientChartResponseDto for this call
// import com.codeskraps.publicpool.data.remote.dto.ClientChartResponseDto
import com.codeskraps.publicpool.data.remote.dto.ClientInfoDto
import com.codeskraps.publicpool.data.remote.dto.NetworkInfoDto
import com.codeskraps.publicpool.data.remote.dto.WalletInfoDto
import com.codeskraps.publicpool.data.remote.dto.BinanceTickerDto // Import Binance DTO
import io.ktor.client.* // Ktor client
import io.ktor.client.call.* // body()
import io.ktor.client.request.* // get()

interface KtorApiService {
    suspend fun getClientInfo(walletAddress: String): ClientInfoDto
    suspend fun getNetworkInfo(): NetworkInfoDto
    // Ensure interface matches implementation return type
    suspend fun getChartData(walletAddress: String): List<ChartDataPointDto>

    // Add new function for blockchain.info
    suspend fun getBlockchainWalletInfo(walletAddress: String): WalletInfoDto

    // Update to Binance endpoint
    suspend fun getTickerPrice(symbol: String = "BTCUSDT"): BinanceTickerDto

    // We can add implementations here or in a separate class
    companion object {
        const val BASE_URL = "https://public-pool.io:40557/api" // Define base URL
        const val BLOCKCHAIN_INFO_BASE_URL = "https://blockchain.info" // New base URL
        const val BINANCE_BASE_URL = "https://api.binance.com" // Binance base URL
    }
}

// Example Implementation (can be provided via Koin later)
class KtorApiServiceImpl(private val client: HttpClient) : KtorApiService {

    override suspend fun getClientInfo(walletAddress: String): ClientInfoDto {
        return client.get("${KtorApiService.BASE_URL}/client/$walletAddress").body()
    }

    override suspend fun getNetworkInfo(): NetworkInfoDto {
        return client.get("${KtorApiService.BASE_URL}/network").body()
    }

    override suspend fun getChartData(walletAddress: String): List<ChartDataPointDto> {
        // Use the correct URL with /chart and expect a List directly
        return client.get("${KtorApiService.BASE_URL}/client/$walletAddress/chart").body<List<ChartDataPointDto>>()
        // Or let type inference work:
        // return client.get("${KtorApiService.BASE_URL}/client/$walletAddress/chart").body()
    }

    // Implementation for new function
    override suspend fun getBlockchainWalletInfo(walletAddress: String): WalletInfoDto {
        return client.get("${KtorApiService.BLOCKCHAIN_INFO_BASE_URL}/address/$walletAddress") {
            parameter("format", "json") // Add format=json parameter
        }.body()
    }

    // Implementation for Binance endpoint
    override suspend fun getTickerPrice(symbol: String): BinanceTickerDto {
        return client.get("${KtorApiService.BINANCE_BASE_URL}/api/v3/ticker/price") {
            parameter("symbol", symbol)
        }.body()
    }
} 