package com.codeskraps.publicpool.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

// Define DataStore instance at the top level
// Use a unique name for the DataStore file
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "public_pool_settings")

val appModule = module {
    // Provide DataStore instance
    single<DataStore<Preferences>> { androidContext().dataStore }
    
    // Provide AppReadinessState as a singleton
    singleOf(::AppReadinessState)
} 