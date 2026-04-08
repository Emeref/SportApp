package com.example.sportapp.presentation.stats

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.sportapp.LocalMobileTexts
import com.example.sportapp.presentation.settings.WidgetItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverallStatsWidgetScreen(
    widgets: List<WidgetItem>,
    charts: List<WidgetItem>,
    onSave: (List<WidgetItem>, List<WidgetItem>) -> Unit,
    onCancel: () -> Unit
) {
    val texts = LocalMobileTexts.current
    var internalWidgets by remember(widgets) { mutableStateOf(widgets) }
    var internalCharts by remember(charts) { mutableStateOf(charts) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = texts.STATS_SETTINGS_TITLE,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onCancel,
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = texts.SETTINGS_CLOSE)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text(
                        text = texts.STATS_SECTION_WIDGETS,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                itemsIndexed(internalWidgets) { index, item ->
                    OverallStatsWidgetRow(
                        item = item,
                        isFirst = index == 0,
                        isLast = index == internalWidgets.size - 1,
                        label = texts.getWidgetLabel(item.id),
                        onMoveUp = {
                            val list = internalWidgets.toMutableList()
                            val temp = list[index]
                            list[index] = list[index - 1]
                            list[index - 1] = temp
                            internalWidgets = list
                        },
                        onMoveDown = {
                            val list = internalWidgets.toMutableList()
                            val temp = list[index]
                            list[index] = list[index + 1]
                            list[index + 1] = temp
                            internalWidgets = list
                        },
                        onCheckedChange = { isEnabled ->
                            internalWidgets = internalWidgets.map {
                                if (it.id == item.id) it.copy(isEnabled = isEnabled) else it
                            }
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = texts.STATS_SECTION_CHARTS,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                itemsIndexed(internalCharts) { index, item ->
                    OverallStatsWidgetRow(
                        item = item,
                        isFirst = index == 0,
                        isLast = index == internalCharts.size - 1,
                        label = texts.getWidgetLabel(item.id),
                        onMoveUp = {
                            val list = internalCharts.toMutableList()
                            val temp = list[index]
                            list[index] = list[index - 1]
                            list[index - 1] = temp
                            internalCharts = list
                        },
                        onMoveDown = {
                            val list = internalCharts.toMutableList()
                            val temp = list[index]
                            list[index] = list[index + 1]
                            list[index + 1] = temp
                            internalCharts = list
                        },
                        onCheckedChange = { isEnabled ->
                            internalCharts = internalCharts.map {
                                if (it.id == item.id) it.copy(isEnabled = isEnabled) else it
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { 
                        onSave(internalWidgets, internalCharts)
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text(texts.SETTINGS_SAVE, color = Color.White)
                }
                Button(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
                ) {
                    Text(texts.SETTINGS_CLOSE, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun OverallStatsWidgetRow(
    item: WidgetItem,
    isFirst: Boolean,
    isLast: Boolean,
    label: String,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onCheckedChange: (Boolean) -> Unit
) {
    val texts = LocalMobileTexts.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = item.isEnabled, onCheckedChange = onCheckedChange)
            Text(
                text = label,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge
            )
            IconButton(onClick = onMoveUp, enabled = !isFirst) {
                Icon(Icons.Default.KeyboardArrowUp, texts.STATS_MOVE_UP)
            }
            IconButton(onClick = onMoveDown, enabled = !isLast) {
                Icon(Icons.Default.KeyboardArrowDown, texts.STATS_MOVE_DOWN)
            }
        }
    }
}
