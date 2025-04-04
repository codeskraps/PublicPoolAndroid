package com.codeskraps.publicpool.di

import com.codeskraps.publicpool.domain.usecase.*
import org.koin.dsl.module

val domainModule = module {
    // Use Case providers
    factory { GetWalletAddressUseCase(repository = get()) }
    factory { SaveWalletAddressUseCase(repository = get()) }
    factory { GetNetworkInfoUseCase(repository = get()) }
    factory { GetClientInfoUseCase(repository = get()) }
    factory { GetChartDataUseCase(repository = get()) }
    factory { CalculateTwoHourAverageUseCase() }
    factory { GetBlockchainWalletInfoUseCase(repository = get()) }
    factory { GetBtcPriceUseCase(repository = get()) }
} 