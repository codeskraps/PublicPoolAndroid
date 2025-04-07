package com.codeskraps.publicpool.presentation.settings

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.codeskraps.publicpool.BuildConfig
import com.codeskraps.publicpool.R
import com.codeskraps.publicpool.presentation.common.AppCard
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContent(screenModel: SettingsScreenModel) {
    val state by screenModel.state.collectAsState() // Collect state from ScreenModel
    val context = LocalContext.current
    val navigator = LocalNavigator.currentOrThrow // Get the navigator
    val focusManager = LocalFocusManager.current // Focus manager to hide keyboard

    // Track page view when screen becomes visible
    LaunchedEffect(Unit) {
        screenModel.handleEvent(SettingsEvent.OnScreenVisible)
    }

    // Resolve strings needed inside LaunchedEffect here
    val walletSavedMessage = stringResource(R.string.settings_toast_wallet_saved)

    // Effect handling (e.g., showing toasts)
    LaunchedEffect(key1 = screenModel.effect) {
        screenModel.effect.collectLatest { effect ->
            when (effect) {
                is SettingsEffect.ShowError -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
                SettingsEffect.WalletAddressSaved -> {
                    // Use the pre-resolved string
                    Toast.makeText(context, walletSavedMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.screen_title_settings)) },
                navigationIcon = {
                    IconButton(onClick = { navigator.pop() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.action_back)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator()
            } else {
                // Wallet Address Section
                OutlinedTextField(
                    value = state.walletAddress,
                    onValueChange = { screenModel.handleEvent(SettingsEvent.WalletAddressChanged(it)) },
                    label = { Text(stringResource(R.string.settings_label_wallet_address)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Button(
                    onClick = { 
                        screenModel.handleEvent(SettingsEvent.SaveWalletAddress)
                        focusManager.clearFocus() // Clear focus to hide keyboard
                    },
                    modifier = Modifier.align(Alignment.End),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(stringResource(R.string.settings_button_save))
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                // App Information Section
                AppCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Public Pool Android",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Text(
                            text = "Version ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        
                        TextButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW)
                                intent.data = "https://codeskraps.com".toUri()
                                context.startActivity(intent)
                            }
                        ) {
                            Text(
                                text = "Developer: codeskraps.com",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        
                        Text(
                            text = "License: MIT",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        
                        Text(
                            text = "Analytics: Self-hosted Umami",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        
                        TextButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW)
                                intent.data =
                                    "https://repo.codeskraps.com/codeskraps/PublicPoolAndroid".toUri()
                                context.startActivity(intent)
                            }
                        ) {
                            Text(
                                text = "Source Repository",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
} 