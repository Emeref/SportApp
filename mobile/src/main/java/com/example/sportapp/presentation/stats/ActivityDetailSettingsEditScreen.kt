package com.example.sportapp.presentation.stats

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sportapp.core.i18n.LocalAppStrings
import com.example.sportapp.presentation.settings.WidgetSelectionRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityDetailSettingsEditScreen(
    viewModel: ActivityDetailSettingsViewModel,
    onNavigateBack: () -> Unit
) {
    val strings = LocalAppStrings.current
    val settings by viewModel.getSettings(strings).collectAsStateWithLifecycle()
    
    var internalCharts by remember(settings.visibleCharts) { mutableStateOf(settings.visibleCharts) }
    var internalWidgets by remember(settings.visibleWidgets) { mutableStateOf(settings.visibleWidgets) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("${strings.activitySettings}: ${viewModel.typeName}") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = strings.back)
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
                        text = strings.widgetsSectionLabel,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                itemsIndexed(internalWidgets) { index, item ->
                    WidgetSelectionRow(
                        item = item,
                        isFirst = index == 0,
                        isLast = index == internalWidgets.size - 1,
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
                        text = strings.chartsSectionLabel,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                itemsIndexed(internalCharts) { index, item ->
                    WidgetSelectionRow(
                        item = item,
                        isFirst = index == 0,
                        isLast = index == internalCharts.size - 1,
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
                        viewModel.saveVisibleCharts(internalCharts)
                        viewModel.saveVisibleWidgets(internalWidgets)
                        onNavigateBack()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text(strings.save, color = Color.White)
                }
                Button(
                    onClick = onNavigateBack,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
                ) {
                    Text(strings.close, color = Color.White)
                }
            }
        }
    }
}
