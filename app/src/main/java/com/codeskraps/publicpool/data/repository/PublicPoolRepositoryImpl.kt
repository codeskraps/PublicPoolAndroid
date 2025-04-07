package com.codeskraps.publicpool.data.repository

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.codeskraps.publicpool.data.local.PreferencesKeys
import com.codeskraps.publicpool.data.remote.KtorApiService
import com.codeskraps.publicpool.data.mappers.toDomain
import com.codeskraps.publicpool.data.mappers.toDomainList
import com.codeskraps.publicpool.domain.model.ChartDataPoint
import com.codeskraps.publicpool.domain.model.ClientInfo
import com.codeskraps.publicpool.domain.model.NetworkInfo
import com.codeskraps.publicpool.domain.model.WalletInfo
import com.codeskraps.publicpool.domain.model.CryptoPrice
import com.codeskraps.publicpool.data.mappers.toCryptoPrice
import com.codeskraps.publicpool.domain.repository.PublicPoolRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class PublicPoolRepositoryImpl(
    private val apiService: KtorApiService,
    private val dataStore: DataStore<Preferences>
) : PublicPoolRepository {

    companion object {
        private const val TAG = "PublicPoolRepository" // Tag for logging
    }

    // --- Network Data ---
    override suspend fun getNetworkInfo(): Result<NetworkInfo> {
        return try {
            val networkInfoDto = apiService.getNetworkInfo()
            Result.success(networkInfoDto.toDomain()) // Map DTO to Domain
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get network info", e)
            Result.failure(e)
        }
    }

    // --- Client Data ---
    override suspend fun getClientInfo(walletAddress: String): Result<ClientInfo> {
        return try {
            val clientInfoDto = apiService.getClientInfo(walletAddress)
            Result.success(clientInfoDto.toDomain())
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get client info for $walletAddress", e)
            Result.failure(e)
        }
    }

    override suspend fun getChartData(walletAddress: String): Result<List<ChartDataPoint>> {
        return try {
            val chartDataDtoList = apiService.getChartData(walletAddress)
            Result.success(chartDataDtoList.toDomainList()) // Map list of DTOs
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get chart data for $walletAddress", e)
            Result.failure(e)
        }
    }

    // --- Wallet Address Management (DataStore) ---
    override fun getWalletAddress(): Flow<String?> {
        return dataStore.data
            .catch { exception ->
                // dataStore.data throws an IOException if it can't read the data
                if (exception is IOException) {
                    Log.e(TAG, "Error reading wallet address from DataStore", exception)
                    emit(emptyPreferences()) // Emit empty preferences on error
                } else {
                    throw exception // Rethrow other exceptions
                }
            }
            .map { preferences ->
                preferences[PreferencesKeys.WALLET_ADDRESS]
            }
    }

    override suspend fun saveWalletAddress(address: String) {
       try {
            dataStore.edit { preferences ->
                preferences[PreferencesKeys.WALLET_ADDRESS] = address
            }
       } catch(e: Exception) {
            Log.e(TAG, "Failed to save wallet address to DataStore", e)
            throw e
       }
    }

    // --- Base URL Management ---
    override fun getBaseUrl(): Flow<String> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    Log.e(TAG, "Error reading base URL from DataStore", exception)
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[PreferencesKeys.BASE_URL] ?: KtorApiService.DEFAULT_BASE_URL
            }
    }

    override suspend fun saveBaseUrl(url: String) {
        try {
            dataStore.edit { preferences ->
                preferences[PreferencesKeys.BASE_URL] = url
            }
        } catch(e: Exception) {
            Log.e(TAG, "Failed to save base URL to DataStore", e)
            throw e
        }
    }

    // --- Blockchain.info Data ---
    override suspend fun getBlockchainWalletInfo(walletAddress: String): Result<WalletInfo> {
        return try {
            val walletInfoDto = apiService.getBlockchainWalletInfo(walletAddress)
            Result.success(walletInfoDto.toDomain()) // Map DTO to Domain
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get blockchain wallet info for $walletAddress", e)
            Result.failure(e)
        }
    }

    // --- Price Data ---
    override suspend fun getBtcPriceUsdt(): Result<CryptoPrice> {
        return try {
            // Call the service function (defaults to BTCUSDT)
            val response = apiService.getTickerPrice()
            // Correct: Pass the QUOTE currency ("USDT") to the mapper
            val cryptoPrice = response.toCryptoPrice(currency = "USDT")
            if (cryptoPrice != null) {
                Result.success(cryptoPrice)
            } else {
                // Correct: Simple error message for Binance parsing failure
                Result.failure(Exception("Failed to parse BTC price from Binance response."))
            }
        } catch (e: Exception) {
            // Correct: Log message should mention Binance
            Log.e(TAG, "Failed to get BTC price from Binance", e)
            Result.failure(e)
        }
    }
}
