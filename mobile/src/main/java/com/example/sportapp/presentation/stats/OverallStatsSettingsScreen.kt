package com.example.sportapp.presentation.stats

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sportapp.LocalMobileTexts
import com.example.sportapp.presentation.settings.WidgetItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverallStatsSettingsScreen(
    onBack: () -> Unit,
    viewModel: OverallStatsSettingsViewModel = hiltViewModel()
) {
    val texts = LocalMobileTexts.current
    val widgets by viewModel.widgets.collectAsState()
    val charts by viewModel.charts.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(texts.STATS_SETTINGS_TITLE) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Widgety
            Text(
                text = texts.STATS_SECTION_WIDGETS,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            widgets.forEachIndexed { index, widget ->
                WidgetSettingsRow(
                    widget = widget,
                    onToggle = { viewModel.toggleWidget(widget.id) },
                    onMoveUp = if (index > 0) { { viewModel.moveWidget(index, index - 1) } } else null,
                    onMoveDown = if (index < widgets.size - 1) { { viewModel.moveWidget(index, index + 1) } } else null
                )
            }

            // Wykresy
            Text(
                text = texts.STATS_SECTION_CHARTS,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            charts.forEachIndexed { index, chart ->
                WidgetSettingsRow(
                    widget = chart,
                    onToggle = { viewModel.toggleChart(chart.id) },
                    onMoveUp = if (index > 0) { { viewModel.moveChart(index, index - 1) } } else null,
                    onMoveDown = if (index < charts.size - 1) { { viewModel.moveChart(index, index + 1) } } else null
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun WidgetSettingsRow(
    widget: WidgetItem,
    onToggle: () -> Unit,
    onMoveUp: (() -> Unit)?,
    onMoveDown: (() -> Unit)?
) {
    val texts = LocalMobileTexts.current
    val label = texts.getWidgetLabel(widget.id)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = widget.isEnabled,
                onCheckedChange = { onToggle() }
            )
            Text(
                text = label,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium
            )
            Row {
                IconButton(onClick = onMoveUp ?: {}, enabled = onMoveUp != null) {
                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = texts.STATS_MOVE_UP)
                }
                IconButton(onClick = onMoveDown ?: {}, enabled = onMoveDown != null) {
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = texts.STATS_MOVE_DOWN)
                }
            }
        }
    }
}
