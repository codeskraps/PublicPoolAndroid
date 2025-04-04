package com.codeskraps.publicpool.di

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

// Simple state holder to signal when initial data is ready for splash screen dismissal
class AppReadinessState {
    private val _isReady = MutableStateFlow(false)
    val isReady = _isReady.asStateFlow()

    fun setReady() {
        _isReady.value = true
    }
} 