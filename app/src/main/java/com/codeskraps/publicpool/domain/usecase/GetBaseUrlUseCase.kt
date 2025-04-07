package com.codeskraps.publicpool.domain.usecase

import com.codeskraps.publicpool.domain.repository.PublicPoolRepository
import kotlinx.coroutines.flow.Flow

class GetBaseUrlUseCase(
    private val repository: PublicPoolRepository
) {
    operator fun invoke(): Flow<String> = repository.getBaseUrl()
} 