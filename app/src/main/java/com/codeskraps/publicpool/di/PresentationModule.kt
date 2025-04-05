package com.codeskraps.publicpool.di

import com.codeskraps.publicpool.presentation.dashboard.DashboardScreenModel
import com.codeskraps.publicpool.presentation.settings.SettingsScreenModel
import com.codeskraps.publicpool.presentation.wallet.WalletScreenModel // Import Wallet ScreenModel
import com.codeskraps.publicpool.presentation.workers.WorkersScreenModel // Import Worker ScreenModel
import org.koin.dsl.module

val presentationModule = module {
    // Voyager ScreenModels (similar to ViewModels)
    factory {
        DashboardScreenModel(
            getWalletAddressUseCase = get(),
            getNetworkInfoUseCase = get(),
            getClientInfoUseCase = get(),
            getChartDataUseCase = get(),
            calculateTwoHourAverageUseCase = get(),
            trackPageViewUseCase = get(),
            trackEventUseCase = get(),
            appReadinessState = get()
        )
    }
    factory { SettingsScreenModel(get(), get()) } // Inject use cases
    factory {
        WorkersScreenModel(
            getWalletAddressUseCase = get(),
            getClientInfoUseCase = get(),
            trackPageViewUseCase = get(),
            trackEventUseCase = get()
        )
    }
    factory {
        WalletScreenModel(
            getWalletAddressUseCase = get(),
            getBlockchainWalletInfoUseCase = get(),
            getBtcPriceUseCase = get(),
            trackPageViewUseCase = get(),
            trackEventUseCase = get()
        )
    }
} 