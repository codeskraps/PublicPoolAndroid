package com.codeskraps.publicpool.data.local

import androidx.datastore.preferences.core.stringPreferencesKey

object PreferencesKeys {
    val WALLET_ADDRESS = stringPreferencesKey("wallet_address")
    val BASE_URL = stringPreferencesKey("base_url")
} 