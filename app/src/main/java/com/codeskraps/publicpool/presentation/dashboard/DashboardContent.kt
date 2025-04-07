package com.codeskraps.publicpool.presentation.dashboard

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.anychart.APIlib
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.charts.Cartesian
import com.anychart.enums.Anchor
import com.anychart.enums.MarkerType
import com.anychart.enums.TooltipPositionMode
import com.anychart.graphics.vector.Stroke
import com.codeskraps.publicpool.R
import com.codeskraps.publicpool.domain.model.ChartDataPoint
import com.codeskraps.publicpool.presentation.common.AppCard
import com.codeskraps.publicpool.presentation.navigation.SettingsScreen
import com.codeskraps.publicpool.presentation.navigation.getParentOrSelf
import com.codeskraps.publicpool.ui.theme.PositiveGreen
import com.codeskraps.publicpool.util.formatHashRate
import com.codeskraps.publicpool.util.formatLargeNumber
import kotlinx.coroutines.flow.collectLatest
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardContent(screenModel: DashboardScreenModel) {
    val state by screenModel.state.collectAsState()
    
    // Get the current navigator, which might be inside a tab
    val navigator = LocalNavigator.currentOrThrow
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Track page view when screen becomes visible
    LaunchedEffect(Unit) {
        screenModel.handleEvent(DashboardEvent.OnScreenVisible)
    }
    
    // Handle effects (navigation, snackbars)
    LaunchedEffect(key1 = screenModel.effect) {
        screenModel.effect.collectLatest { effect ->
            when (effect) {
                DashboardEffect.NavigateToSettings -> {
                    // Get the parent/root navigator and push SettingsScreen to it
                    // This ensures we exit tab navigation when going to settings
                    navigator.getParentOrSelf().push(SettingsScreen)
                }
                is DashboardEffect.ShowErrorSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = effect.message,
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.screen_title_dashboard)) },
                actions = {
                    // Show loading indicator in TopAppBar if any data is loading
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(Modifier.width(8.dp))
                    }
                    IconButton(onClick = { screenModel.handleEvent(DashboardEvent.GoToSettings) }) {
                        Icon(Icons.Filled.Settings, contentDescription = stringResource(R.string.dashboard_action_settings))
                    }
                }
            )
        }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = state.isLoading,
            onRefresh = { screenModel.handleEvent(DashboardEvent.RefreshData) },
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    start = 0.dp,
                    end = 0.dp,
                    bottom = 0.dp
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Add padding between TopAppBar and first card row
                Spacer(modifier = Modifier.height(16.dp))

                // Show message if no wallet address is set
                if (!state.isWalletLoading && (state.walletAddress?.isBlank() != false)) {
                    AppCard(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
                        Text(
                            text = stringResource(R.string.dashboard_info_set_wallet),
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                // Top Info Cards Row/Grid
                TopInfoCards(state = state)

                Spacer(modifier = Modifier.height(16.dp))

                // Placeholder for Workers List (Add later if API provides worker data)
                // WorkersSection(state = state)

                // Chart Section
                ChartSection(state = state)

                // Bottom padding that won't extend beyond the visible area
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun TopInfoCards(state: DashboardState) {
    val numberFormat = remember { NumberFormat.getNumberInstance(Locale.US) }

    // Using Row with weights for responsiveness, consider Grid for more items
    Row(modifier = Modifier.fillMaxWidth()) {
        InfoCard(
            label = stringResource(R.string.dashboard_card_label_your_best_difficulty),
            value = state.clientInfo?.bestDifficulty?.toDoubleOrNull()?.let { formatLargeNumber(it) } ?: state.clientInfo?.bestDifficulty ?: stringResource(R.string.text_placeholder_dash),
            secondaryValue = state.clientInfo?.bestDifficulty?.toDoubleOrNull()?.let { numberFormat.format(it) },
            isLoading = state.isClientInfoLoading,
            modifier = Modifier.weight(1f),
            showInfoIcon = true,
        )
        Spacer(modifier = Modifier.width(8.dp))
        InfoCard(
            label = stringResource(R.string.dashboard_card_label_network_difficulty),
            value = formatLargeNumber(state.networkInfo?.networkDifficulty ?: 0.0),
            secondaryValue = numberFormat.format(state.networkInfo?.networkDifficulty ?: 0.0),
            isLoading = state.isNetworkLoading,
            modifier = Modifier.weight(1f),
        )
    }
    Spacer(modifier = Modifier.height(8.dp))
    Row(modifier = Modifier.fillMaxWidth()) {
        InfoCard(
            label = stringResource(R.string.dashboard_card_label_network_hash_rate),
            value = formatHashRate(state.networkInfo?.networkHashRate ?: 0.0),
            isLoading = state.isNetworkLoading,
            modifier = Modifier.weight(1f),
        )
        Spacer(modifier = Modifier.width(8.dp))
        InfoCard(
            label = stringResource(R.string.dashboard_card_label_block_height),
            value = numberFormat.format(state.networkInfo?.blockHeight ?: 0L),
            secondaryValue = "${stringResource(R.string.dashboard_card_secondary_block_weight_prefix)} ${numberFormat.format(state.networkInfo?.blockWeight ?: 0L)}",
            isLoading = state.isNetworkLoading,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
fun InfoCard(
    label: String,
    value: String,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    secondaryValue: String? = null,
    showInfoIcon: Boolean = false,
) {
    val showDialog = remember { mutableStateOf(false) }
    
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = null,
            text = { Text(text = stringResource(R.string.difficulty_explanation)) },
            confirmButton = { 
                TextButton(onClick = { showDialog.value = false }) {
                    Text(stringResource(R.string.action_close))
                }
            }
        )
    }
    
    AppCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 100.dp)
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = label, 
                    style = MaterialTheme.typography.labelMedium, 
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                if (showInfoIcon) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = stringResource(R.string.info_icon_description),
                        modifier = Modifier
                            .size(20.dp)
                            .clickable(
                                onClick = { showDialog.value = true },
                                indication = null,
                                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                            )
                            .padding(2.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Box(modifier = Modifier.align(Alignment.End)) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                } else {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = value,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (!secondaryValue.isNullOrEmpty()) {
                            Text(
                                text = secondaryValue,
                                style = MaterialTheme.typography.bodySmall,
                                color = PositiveGreen
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChartSection(state: DashboardState) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(vertical = 16.dp)) { // Padding only vertical
            Text(
                text = stringResource(R.string.dashboard_chart_title),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            val chartContainerModifier = Modifier
                .fillMaxWidth()
                .height(250.dp)

            when {
                state.isChartDataLoading -> {
                    Box(modifier = chartContainerModifier, contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                state.chartData.isNotEmpty() -> {
                    HashRateChart(
                        tenMinData = state.chartData,
                        twoHourData = state.chartDataTwoHourAvg, // Pass 2h data
                        modifier = chartContainerModifier
                    )
                }
                !state.isWalletLoading && !state.walletAddress.isNullOrBlank() -> {
                     // Wallet is set, not loading, but chart data is empty
                     Box(modifier = chartContainerModifier.padding(horizontal = 16.dp), contentAlignment = Alignment.Center) {
                        Text(stringResource(R.string.dashboard_chart_no_data))
                    }
                }
                else -> {
                    // Placeholder when wallet isn't set or still loading wallet
                     Box(modifier = chartContainerModifier.padding(horizontal = 16.dp), contentAlignment = Alignment.Center) {
                        // Avoid showing loading text if wallet address just needs to be entered
                        if (!state.isWalletLoading) {
                            Text(stringResource(R.string.dashboard_chart_set_wallet))
                        }
                     }
                }
            }
        }
    }
}

@Composable
fun HashRateChart(
    tenMinData: List<ChartDataPoint>,
    twoHourData: List<ChartDataPoint>,
    modifier: Modifier = Modifier
) {
    // Define colors from theme or directly
    val surfaceColorHex = "#${MaterialTheme.colorScheme.surface.toArgb().toUInt().toString(16).substring(2)}" // Get hex like #1F2C40
    val onSurfaceVariantColorHex = "#${MaterialTheme.colorScheme.onSurfaceVariant.toArgb().toUInt().toString(16).substring(2)}" // Get hex like #A2A6AC
    val line10MinColorHex = "#6cbcd0" // Light Blue
    val line2HourColorHex = "#d5a326" // Yellow/Gold

    // Resolve strings outside AndroidView
    val series10MinName = stringResource(R.string.dashboard_chart_series_10min)
    val series2HourName = stringResource(R.string.dashboard_chart_series_2hour)

    // Use update lambda of AndroidView for configuration
    AndroidView(
        factory = { ctx -> AnyChartView(ctx) },
        modifier = modifier,
        update = { view ->
            Log.d("HashRateChart", "Updating AnyChart styles and data")

            // Prepare data (as before)
            val seriesData10Min = tenMinData.map {
                ValueDataEntry(it.timestamp.toInstant().toEpochMilli(), it.hashRate)
            }
            val seriesData2Hour = twoHourData.map {
                ValueDataEntry(it.timestamp.toInstant().toEpochMilli(), it.hashRate)
            }

            val cartesian: Cartesian = AnyChart.line()
            APIlib.getInstance().setActiveAnyChartView(view)

            // --- Styling --- >
            cartesian.background().fill(surfaceColorHex) // Set background color
            cartesian.animation(true)
            cartesian.padding(10.0, 20.0, 5.0, 5.0)

            cartesian.crosshair().enabled(true)
            cartesian.crosshair()
                .yLabel(true)
                .yStroke(null as Stroke?, null, null, null as String?, null as String?)

            cartesian.tooltip().positionMode(TooltipPositionMode.POINT)

            // Axis Styling
            cartesian.yAxis(0).title(false)
            cartesian.xAxis(0).labels().padding(5.0, 5.0, 5.0, 5.0)
            cartesian.xAxis(0).labels().format("{%Value}{dateTimeFormat:HH:mm}")
            cartesian.xAxis(0).labels().fontColor(onSurfaceVariantColorHex) // Set X-axis label color
            cartesian.yAxis(0).labels().format("{%Value}{scale:(1000)(1000)(1000)(1000)|( H/s)( KH/s)( MH/s)( GH/s)( TH/s)}")
            cartesian.yAxis(0).labels().fontColor(onSurfaceVariantColorHex) // Set Y-axis label color

            // Grid lines (optional, set color if desired)
            // cartesian.yGrid(0).stroke("#ffffff 0.1")
            // cartesian.xGrid(0).stroke("#ffffff 0.1")

            // --- Series Configuration --- >
            // Series 1: 10 Minute (Blue)
            val series10Min = cartesian.line(seriesData10Min)
            series10Min.name(series10MinName)
            series10Min.color(line10MinColorHex) // Use defined blue color
            series10Min.hovered().markers().enabled(true)
            series10Min.hovered().markers().type(MarkerType.CIRCLE).size(4.0)
            series10Min.tooltip()
                .position("right")
                .anchor(Anchor.LEFT_CENTER)
                .offsetX(5.0)
                .offsetY(5.0)
                .format("10m - {%x}{dateTimeFormat:dd MMM HH:mm}: {%Value}{scale:(1000)(1000)(1000)(1000)|( H/s)( KH/s)( MH/s)( GH/s)( TH/s)}")

            // Series 2: 2 Hour (Yellow/Gold)
            if (seriesData2Hour.isNotEmpty()) {
                val series2Hour = cartesian.line(seriesData2Hour)
                series2Hour.name(series2HourName)
                series2Hour.color(line2HourColorHex) // Use defined yellow color
                series2Hour.hovered().markers().enabled(true)
                series2Hour.hovered().markers().type(MarkerType.CIRCLE).size(4.0)
                series2Hour.tooltip()
                    .position("right")
                    .anchor(Anchor.LEFT_CENTER)
                    .offsetX(5.0)
                    .offsetY(5.0)
                    .format("2h - {%x}{dateTimeFormat:dd MMM HH:mm}: {%Value}{scale:(1000)(1000)(1000)(1000)|( H/s)( KH/s)( MH/s)( GH/s)( TH/s)}")
            }

            // --- Final Chart Setup --- >
            cartesian.xScale(com.anychart.scales.DateTime.instantiate())
            cartesian.legend().enabled(true)
            cartesian.legend().fontColor(onSurfaceVariantColorHex) // Set legend text color
            cartesian.legend().fontSize(13.0)
            cartesian.legend().padding(0.0, 0.0, 10.0, 0.0)

            // Remove "AnyChart Trial Version" watermark
            view.setLicenceKey("your-organization-name,com.codeskraps.publicpool-1,ORwODQEtD24EL24OTwlwBGELBQ==")

            view.setChart(cartesian)
        }
    )
}
