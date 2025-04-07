package com.codeskraps.publicpool.domain.repository

import com.codeskraps.publicpool.domain.model.ChartDataPoint
import com.codeskraps.publicpool.domain.model.ClientInfo
import com.codeskraps.publicpool.domain.model.NetworkInfo
import com.codeskraps.publicpool.domain.model.WalletInfo
import com.codeskraps.publicpool.domain.model.CryptoPrice
import kotlinx.coroutines.flow.Flow

interface PublicPoolRepository {

    // --- Network Data ---
    suspend fun getNetworkInfo(): Result<NetworkInfo> // Use Result for error handling

    // --- Client Data ---
    suspend fun getClientInfo(walletAddress: String): Result<ClientInfo>
    suspend fun getChartData(walletAddress: String): Result<List<ChartDataPoint>>

    // --- Wallet Address Management (DataStore) ---
    fun getWalletAddress(): Flow<String?> // Flow to observe changes
    suspend fun saveWalletAddress(address: String)

    // --- Base URL Management ---
    fun getBaseUrl(): Flow<String>
    suspend fun saveBaseUrl(url: String)

    // --- Blockchain.info Data ---
    suspend fun getBlockchainWalletInfo(walletAddress: String): Result<WalletInfo>

    // --- Price Data ---
    suspend fun getBtcPriceUsdt(): Result<CryptoPrice>
} 