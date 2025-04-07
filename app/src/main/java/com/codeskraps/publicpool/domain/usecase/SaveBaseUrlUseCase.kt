package com.codeskraps.publicpool.domain.usecase

import com.codeskraps.publicpool.domain.repository.PublicPoolRepository

class SaveBaseUrlUseCase(
    private val repository: PublicPoolRepository
) {
    suspend operator fun invoke(url: String) = repository.saveBaseUrl(url)
} 