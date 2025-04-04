package com.codeskraps.publicpool.presentation.wallet

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Parcelable
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import com.codeskraps.publicpool.R
import com.codeskraps.publicpool.domain.model.CryptoPrice
import com.codeskraps.publicpool.domain.model.WalletInfo
import com.codeskraps.publicpool.domain.model.WalletTransaction
import com.codeskraps.publicpool.presentation.common.AppCard
import com.codeskraps.publicpool.ui.theme.PositiveGreen
import kotlinx.coroutines.flow.collectLatest
import kotlinx.parcelize.Parcelize
import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.util.Currency
import java.util.Locale

@Parcelize
data object WalletScreen : Screen, Parcelable {
    private fun readResolve(): Any = WalletScreen

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val screenModel: WalletScreenModel = koinScreenModel()
        val state by screenModel.state.collectAsState()
        val context = LocalContext.current

        LaunchedEffect(key1 = screenModel.effect) {
            screenModel.effect.collectLatest { effect ->
                when (effect) {
                    is WalletEffect.ShowError -> {
                        Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        Scaffold(
            topBar = { TopAppBar(title = { Text(stringResource(R.string.screen_title_wallet_details)) }) }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when {
                    state.isWalletLoading || state.isLoading -> {
                        // Combined loading indicator
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    state.walletAddress.isNullOrBlank() -> {
                        // Use stringResource for message
                        Text(
                            text = stringResource(R.string.wallet_error_not_set),
                            modifier = Modifier.align(Alignment.Center).padding(16.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                    state.errorMessage != null -> {
                        // Use stringResource for generic error, keep specific from state
                        Text(
                            text = state.errorMessage ?: stringResource(R.string.error_unknown),
                            modifier = Modifier.align(Alignment.Center).padding(16.dp),
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                    }
                    state.walletInfo == null -> {
                         // Use stringResource for message
                         Text(
                            text = stringResource(R.string.wallet_error_load_failed),
                            modifier = Modifier.align(Alignment.Center).padding(16.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                    else -> {
                        // Display Wallet Info and Transactions
                        WalletDetailsContent(
                            walletInfo = state.walletInfo!!,
                            btcPrice = state.btcPrice
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WalletDetailsContent(
    walletInfo: WalletInfo,
    btcPrice: CryptoPrice?
) {
    val context = LocalContext.current
    val btcFormat = remember { "%.8f BTC" }
    val dateFormatter = remember { DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm") }
    val displayCurrency = stringResource(R.string.currency_usd)
    val currencyFormat = remember {
        NumberFormat.getCurrencyInstance(Locale.US).apply {
            currency = Currency.getInstance(displayCurrency)
            maximumFractionDigits = 2
            minimumFractionDigits = 2
        }
    }

    val finalBalanceFiat = remember(walletInfo.finalBalanceBtc, btcPrice) {
        btcPrice?.let { walletInfo.finalBalanceBtc * it.price }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Current Price Section
        item {
            CurrentPriceCard(btcPrice = btcPrice, currencyFormat = currencyFormat)
        }

        // Balance Section
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                BalanceCard(
                    label = stringResource(R.string.wallet_label_final_balance),
                    valueBtc = btcFormat.format(Locale.US, walletInfo.finalBalanceBtc),
                    valueFiat = finalBalanceFiat?.let { currencyFormat.format(it) },
                    fiatCurrencyLabel = btcPrice?.currency ?: stringResource(R.string.currency_usd),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Totals Section
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                BalanceCard(
                    label = stringResource(R.string.wallet_label_total_received),
                    valueBtc = btcFormat.format(Locale.US, walletInfo.totalReceivedBtc),
                    modifier = Modifier.weight(1f)
                )
                BalanceCard(
                    label = stringResource(R.string.wallet_label_total_sent),
                    valueBtc = btcFormat.format(Locale.US, walletInfo.totalSentBtc),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Transaction Header
        item {
            Text(
                text = stringResource(R.string.wallet_header_recent_transactions_count, walletInfo.transactionCount),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 8.dp) // Add some space before tx list
            )
        }

        // Transaction List
        items(walletInfo.transactions, key = { it.hash }) { tx ->
            TransactionItem(tx = tx, dateFormatter = dateFormatter)
        }
    }
}

@Composable
fun CurrentPriceCard(btcPrice: CryptoPrice?, currencyFormat: NumberFormat) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 90.dp)
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.wallet_label_current_btc_price),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Box(modifier = Modifier.align(Alignment.End)) {
                if (btcPrice != null) {
                    Text(
                        text = currencyFormat.format(btcPrice.price),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                } else {
                    Text(
                        text = stringResource(R.string.text_placeholder_dash),
                         style = MaterialTheme.typography.headlineSmall,
                         fontWeight = FontWeight.Bold,
                         color = MaterialTheme.colorScheme.onSurfaceVariant
                     )
                }
            }
        }
    }
}

@Composable
fun BalanceCard(
    label: String,
    valueBtc: String,
    valueFiat: String? = null,
    fiatCurrencyLabel: String? = null,
    modifier: Modifier = Modifier
) {
    AppCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 90.dp)
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Box(modifier = Modifier.align(Alignment.End)) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = valueBtc,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = valueFiat?.let { "${stringResource(R.string.wallet_balance_fiat_prefix)} $it${fiatCurrencyLabel?.let { c -> " $c" } ?: ""}" } ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (valueFiat != null) PositiveGreen else Color.Transparent
                    )
                }
            }
        }
    }
}

@Composable
fun TransactionItem(tx: WalletTransaction, dateFormatter: DateTimeFormatter) {
    val btcFormat = remember { "%.8f BTC" }
    val netValueFormatted = btcFormat.format(Locale.US, tx.resultBtc)
    val timeFormatted = tx.time?.format(dateFormatter) ?: "Pending"
    val valueColor = if (tx.resultSatoshis >= 0) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error // Green for positive, Red for negative

    AppCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = timeFormatted, style = MaterialTheme.typography.labelMedium)
                Text(text = netValueFormatted, style = MaterialTheme.typography.bodyMedium, color = valueColor)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = tx.hash,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// Helper function to copy text to clipboard
fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    // Use context.getString() for non-composable contexts
    val label = context.getString(R.string.wallet_clipboard_label)
    val message = context.getString(R.string.wallet_toast_address_copied)

    val clip = ClipData.newPlainText(label, text)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
} 