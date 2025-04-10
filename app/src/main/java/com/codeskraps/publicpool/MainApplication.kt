package com.codeskraps.publicpool // Make sure package matches your project

import android.app.Application
import com.codeskraps.publicpool.di.appModule
import com.codeskraps.publicpool.di.dataModule
import com.codeskraps.publicpool.di.domainModule
import com.codeskraps.publicpool.di.presentationModule
import com.codeskraps.publicpool.domain.usecase.GetWalletAddressUseCase
import com.codeskraps.publicpool.domain.usecase.IdentifyUserUseCase
import com.codeskraps.publicpool.domain.usecase.InitializeAnalyticsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.logger.Level
import org.koin.java.KoinJavaComponent.inject

class MainApplication : Application() {
    
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val initializeAnalyticsUseCase: InitializeAnalyticsUseCase by inject(InitializeAnalyticsUseCase::class.java)
    private val getWalletAddressUseCase: GetWalletAddressUseCase by inject(GetWalletAddressUseCase::class.java)
    private val identifyUserUseCase: IdentifyUserUseCase by inject(IdentifyUserUseCase::class.java)
    
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.DEBUG) // Use Level.INFO for release builds
            androidContext(this@MainApplication)
            modules(
                appModule, // General app-level dependencies (like DataStore)
                dataModule, // Data layer dependencies (API, DB, Repositories)
                domainModule, // Domain layer dependencies (Use Cases)
                presentationModule // Add presentation module
                // Add presentationModule later for ViewModels
            )
        }
        
        // Initialize analytics and identify user
        applicationScope.launch {
            // First initialize analytics
            initializeAnalyticsUseCase()
            
            // Then retrieve wallet address and identify user if available
            getWalletAddressUseCase()
                .catch { /* Ignore errors during startup */ }
                .firstOrNull()
                ?.let { walletAddress ->
                    // Identify user with their wallet address
                    identifyUserUseCase(walletAddress)
                }
        }
    }
} 