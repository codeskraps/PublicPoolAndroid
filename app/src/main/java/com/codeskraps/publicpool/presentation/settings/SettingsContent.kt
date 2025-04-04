package com.codeskraps.publicpool.presentation.settings

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack // Use AutoMirrored for LTR/RTL
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.codeskraps.publicpool.R
import com.codeskraps.publicpool.presentation.common.AppCard // Import AppCard
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContent(screenModel: SettingsScreenModel) {
    val state by screenModel.state.collectAsState() // Collect state from ScreenModel
    val context = LocalContext.current
    val navigator = LocalNavigator.currentOrThrow // Get the navigator

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
                OutlinedTextField(
                    value = state.walletAddress,
                    onValueChange = { screenModel.handleEvent(SettingsEvent.WalletAddressChanged(it)) },
                    label = { Text(stringResource(R.string.settings_label_wallet_address)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Button(
                    onClick = { screenModel.handleEvent(SettingsEvent.SaveWalletAddress) },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(stringResource(R.string.settings_button_save))
                }
            }
        }
    }
} 