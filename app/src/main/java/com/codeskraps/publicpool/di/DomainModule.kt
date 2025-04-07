package com.codeskraps.publicpool.di

import com.codeskraps.publicpool.domain.usecase.CalculateTwoHourAverageUseCase
import com.codeskraps.publicpool.domain.usecase.GetBaseUrlUseCase
import com.codeskraps.publicpool.domain.usecase.GetBlockchainWalletInfoUseCase
import com.codeskraps.publicpool.domain.usecase.GetBtcPriceUseCase
import com.codeskraps.publicpool.domain.usecase.GetChartDataUseCase
import com.codeskraps.publicpool.domain.usecase.GetClientInfoUseCase
import com.codeskraps.publicpool.domain.usecase.GetNetworkInfoUseCase
import com.codeskraps.publicpool.domain.usecase.GetWalletAddressUseCase
import com.codeskraps.publicpool.domain.usecase.IdentifyUserUseCase
import com.codeskraps.publicpool.domain.usecase.InitializeAnalyticsUseCase
import com.codeskraps.publicpool.domain.usecase.SaveBaseUrlUseCase
import com.codeskraps.publicpool.domain.usecase.SaveWalletAddressUseCase
import com.codeskraps.publicpool.domain.usecase.TrackEventUseCase
import com.codeskraps.publicpool.domain.usecase.TrackPageViewUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val domainModule = module {
    // Use Case providers
    factoryOf(::GetWalletAddressUseCase)
    factoryOf(::SaveWalletAddressUseCase)
    factoryOf(::GetBaseUrlUseCase)
    factoryOf(::SaveBaseUrlUseCase)
    factoryOf(::GetNetworkInfoUseCase)
    factoryOf(::GetClientInfoUseCase)
    factoryOf(::GetChartDataUseCase)
    factoryOf(::CalculateTwoHourAverageUseCase)
    factoryOf(::GetBlockchainWalletInfoUseCase)
    factoryOf(::GetBtcPriceUseCase)
    
    // Analytics
    factoryOf(::InitializeAnalyticsUseCase)
    factoryOf(::TrackPageViewUseCase)
    factoryOf(::TrackEventUseCase)
    factoryOf(::IdentifyUserUseCase)
} 