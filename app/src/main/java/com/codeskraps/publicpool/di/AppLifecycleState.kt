package com.codeskraps.publicpool.di

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppLifecycleState {
    private val _isAppInBackground = MutableStateFlow(false)
    val isAppInBackground: StateFlow<Boolean> = _isAppInBackground.asStateFlow()

    fun setAppInBackground(value: Boolean) {
        _isAppInBackground.value = value
    }
} 