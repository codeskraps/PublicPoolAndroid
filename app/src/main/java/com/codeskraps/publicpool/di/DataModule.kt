package com.codeskraps.publicpool.di

import com.codeskraps.publicpool.data.remote.KtorApiService
import com.codeskraps.publicpool.data.remote.KtorApiServiceImpl
import com.codeskraps.publicpool.data.repository.PublicPoolRepositoryImpl
import com.codeskraps.publicpool.domain.repository.PublicPoolRepository
import io.ktor.client.* // Ktor client
import io.ktor.client.engine.android.* // Ktor Android engine
import io.ktor.client.plugins.contentnegotiation.* // Ktor Content Negotiation
import io.ktor.client.plugins.logging.* // Ktor Logging
import io.ktor.serialization.kotlinx.json.* // Ktor Kotlinx Serialization
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val dataModule = module {

    // Ktor HTTP Client
    single<HttpClient> {
        HttpClient(Android) { // Or CIO, OkHttp if preferred and added dependency
            expectSuccess = true // Optional: Throw exception for non-2xx responses

            // Logging
            install(Logging) {
                logger = Logger.DEFAULT // Simple logger
                level = LogLevel.ALL // Log everything during development
            }

            // JSON Serialization/Deserialization
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true // Important for API changes
                })
            }
        }
    }

    // API Service Implementation
    single<KtorApiService> { KtorApiServiceImpl(client = get()) }

    // Repository Implementation
    single<PublicPoolRepository> { PublicPoolRepositoryImpl(apiService = get(), dataStore = get()) }
} 