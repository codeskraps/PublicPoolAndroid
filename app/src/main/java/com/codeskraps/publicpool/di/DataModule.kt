package com.codeskraps.publicpool.di

import com.codeskraps.publicpool.data.remote.KtorApiService
import com.codeskraps.publicpool.data.remote.KtorApiServiceImpl
import com.codeskraps.publicpool.data.remote.UmamiAnalyticsDataSource
import com.codeskraps.publicpool.data.remote.UmamiConfig
import com.codeskraps.publicpool.data.repository.AnalyticsRepositoryImpl
import com.codeskraps.publicpool.data.repository.PublicPoolRepositoryImpl
import com.codeskraps.publicpool.domain.repository.AnalyticsRepository
import com.codeskraps.publicpool.domain.repository.PublicPoolRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
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
    singleOf(::KtorApiServiceImpl) bind KtorApiService::class

    // Repository Implementation
    singleOf(::PublicPoolRepositoryImpl) bind PublicPoolRepository::class

    // Analytics Configuration
    single { 
        UmamiConfig(
            scriptUrl = "https://umami.codeskraps.com/script.js",
            websiteId = "b3e6309f-9724-48e5-a1c6-11757de3fe83",
            baseUrl = "https://umami.codeskraps.com"
        )
    }

    // Analytics Implementation
    singleOf(::UmamiAnalyticsDataSource)
    singleOf(::AnalyticsRepositoryImpl) bind AnalyticsRepository::class
} 