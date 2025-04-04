package com.codeskraps.publicpool.di

import com.codeskraps.publicpool.presentation.dashboard.DashboardScreenModel
import com.codeskraps.publicpool.presentation.settings.SettingsScreenModel
import com.codeskraps.publicpool.presentation.wallet.WalletScreenModel // Import Wallet ScreenModel
import com.codeskraps.publicpool.presentation.workers.WorkersScreenModel // Import Worker ScreenModel
import org.koin.dsl.module

val presentationModule = module {
    // Voyager ScreenModels (similar to ViewModels)
    factory { DashboardScreenModel(get(), get(), get(), get(), get(), get()) } // Add 6th `get()` for AppReadinessState
    factory { SettingsScreenModel(get(), get()) } // Inject use cases
    factory { WorkersScreenModel(get(), get()) } // Provide WorkersScreenModel
    factory { WalletScreenModel(get(), get(), get()) } // Provide WalletScreenModel (add 3rd get)
} 