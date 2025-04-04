package com.codeskraps.publicpool.presentation.workers

import android.os.Parcelable
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import com.codeskraps.publicpool.R
import com.codeskraps.publicpool.domain.model.Worker
import com.codeskraps.publicpool.util.calculateUptime
import com.codeskraps.publicpool.util.formatDifficulty
import com.codeskraps.publicpool.util.formatHashRate
import com.codeskraps.publicpool.util.formatRelativeTime
import kotlinx.coroutines.flow.collectLatest
import kotlinx.parcelize.Parcelize

@Parcelize
data object WorkersScreen : Screen, Parcelable {
    private fun readResolve(): Any = WorkersScreen

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<WorkersScreenModel>()
        val state by screenModel.state.collectAsState()
        val context = LocalContext.current

        LaunchedEffect(key1 = screenModel.effect) {
            screenModel.effect.collectLatest { effect ->
                when (effect) {
                    is WorkersEffect.ShowError -> {
                        Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        Scaffold(
            topBar = { TopAppBar(title = { Text(stringResource(id = R.string.screen_title_workers)) }) }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when {
                    state.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    state.errorMessage != null -> {
                        Text(
                            text = state.errorMessage ?: stringResource(R.string.error_unknown),
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    state.groupedWorkers.isEmpty() && !state.isWalletLoading -> {
                        Text(
                            text = if (state.walletAddress.isNullOrBlank())
                                stringResource(R.string.workers_no_wallet)
                            else
                                stringResource(R.string.workers_no_data),
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(16.dp),
                            textAlign = TextAlign.Center
                        )
                    }

                    else -> {
                        WorkersList(groupedWorkers = state.groupedWorkers)
                    }
                }
            }
        }
    }
}

@Composable
fun WorkersList(groupedWorkers: Map<String, List<Worker>>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(groupedWorkers.entries.toList(), key = { it.key }) { (workerName, sessions) ->
            WorkerGroupCard(workerName = workerName, sessions = sessions)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun WorkerGroupCard(workerName: String, sessions: List<Worker>) {
    var expanded by remember { mutableStateOf(false) }

    val totalHashRate = sessions.sumOf { it.hashRate ?: 0.0 }
    val groupBestDifficulty = sessions.mapNotNull { it.bestDifficulty }.maxOrNull()

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .animateContentSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = if (expanded) R.drawable.expand_less else R.drawable.expand_more),
                    contentDescription = if (expanded) stringResource(R.string.content_description_collapse)
                    else stringResource(R.string.content_description_expand),
                    modifier = Modifier.size(24.dp),
                    tint = LocalContentColor.current
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    workerName,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        stringResource(R.string.workers_label_session_count, sessions.size),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(formatHashRate(totalHashRate), style = MaterialTheme.typography.bodySmall)
                    Text(
                        formatDifficulty(groupBestDifficulty),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            if (expanded) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)) {
                    Text(
                        stringResource(R.string.workers_header_session_id),
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.labelSmall
                    )
                    Text(
                        stringResource(R.string.workers_header_hash_rate),
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.End
                    )
                    Text(
                        stringResource(R.string.workers_header_difficulty),
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.End
                    )
                    Text(
                        stringResource(R.string.workers_header_uptime),
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.End
                    )
                    Text(
                        stringResource(R.string.workers_header_last_seen),
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.End
                    )
                }
                sessions.forEach { session ->
                    WorkerSessionItem(session = session)
                }
            }
        }
    }
}

@Composable
fun WorkerSessionItem(session: Worker) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            session.sessionId ?: stringResource(R.string.text_placeholder_dash),
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            formatHashRate(session.hashRate ?: 0.0),
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.End
        )
        Text(
            formatDifficulty(session.bestDifficulty),
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.End
        )
        Text(
            calculateUptime(session.startTime),
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.End
        )
        Text(
            formatRelativeTime(session.lastSeen),
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.End
        )
    }
} 