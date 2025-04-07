package com.codeskraps.publicpool.di

import com.codeskraps.publicpool.presentation.dashboard.DashboardScreenModel
import com.codeskraps.publicpool.presentation.settings.SettingsScreenModel
import com.codeskraps.publicpool.presentation.wallet.WalletScreenModel // Import Wallet ScreenModel
import com.codeskraps.publicpool.presentation.workers.WorkersScreenModel // Import Worker ScreenModel
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val presentationModule = module {
    factoryOf(::DashboardScreenModel)
    factoryOf(::SettingsScreenModel)
    factoryOf(::WorkersScreenModel)
    factoryOf(::WalletScreenModel)
} 