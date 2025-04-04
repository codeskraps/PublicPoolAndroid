package com.codeskraps.publicpool // Make sure package matches your project

import android.app.Application
import com.codeskraps.publicpool.di.appModule
import com.codeskraps.publicpool.di.dataModule
import com.codeskraps.publicpool.di.domainModule
import com.codeskraps.publicpool.di.presentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.logger.Level

class MainApplication : Application() {
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
    }
} 