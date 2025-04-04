package com.codeskraps.publicpool.domain.usecase

import com.codeskraps.publicpool.domain.model.NetworkInfo
import com.codeskraps.publicpool.domain.repository.PublicPoolRepository

class GetNetworkInfoUseCase(private val repository: PublicPoolRepository) {
    suspend operator fun invoke(): Result<NetworkInfo> {
        return repository.getNetworkInfo()
    }
} 