package com.example.sportapp.presentation.stats

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sportapp.core.i18n.LocalAppStrings
import com.example.sportapp.presentation.settings.WidgetItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverallStatsWidgetScreen(
    widgets: List<WidgetItem>,
    charts: List<WidgetItem>,
    onSave: (List<WidgetItem>, List<WidgetItem>) -> Unit,
    onCancel: () -> Unit
) {
    val strings = LocalAppStrings.current
    var currentWidgets by remember(widgets) { mutableStateOf(widgets) }
    var currentCharts by remember(charts) { mutableStateOf(charts) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(strings.widgetSelectionTitle) },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = strings.back)
                    }
                },
                actions = {
                    IconButton(onClick = { onSave(currentWidgets, currentCharts) }) {
                        Icon(Icons.Default.Check, contentDescription = strings.save)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text(
                    text = strings.widgetsSectionLabel,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            items(currentWidgets) { item ->
                WidgetSelectionItem(
                    item = item,
                    onToggle = { isEnabled ->
                        currentWidgets = currentWidgets.map {
                            if (it.id == item.id) it.copy(isEnabled = isEnabled) else it
                        }
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = strings.chartsSectionLabel,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            items(currentCharts) { item ->
                WidgetSelectionItem(
                    item = item,
                    onToggle = { isEnabled ->
                        currentCharts = currentCharts.map {
                            if (it.id == item.id) it.copy(isEnabled = isEnabled) else it
                        }
                    }
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun WidgetSelectionItem(
    item: WidgetItem,
    onToggle: (Boolean) -> Unit
) {
    Surface(
        onClick = { onToggle(!item.isEnabled) },
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = if (item.isEnabled) 1f else 0.5f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(item.label, style = MaterialTheme.typography.bodyLarge)
            Switch(checked = item.isEnabled, onCheckedChange = onToggle)
        }
    }
}
