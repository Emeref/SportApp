package com.example.sportapp.presentation.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.sportapp.LocalMobileTexts

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WidgetSelectionScreen(
    widgets: List<WidgetItem>,
    title: String,
    initialDays: Int? = null,
    daysLabel: String? = null,
    onSave: (List<WidgetItem>, Int?) -> Unit,
    onCancel: () -> Unit
) {
    val texts = LocalMobileTexts.current
    // Używamy remember(widgets), aby zaktualizować listę, gdy dane zostaną załadowane z DataStore
    var internalWidgets by remember(widgets) { mutableStateOf(widgets) }
    var customDays by remember(initialDays) { mutableStateOf(initialDays?.toString() ?: "") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title,
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
                itemsIndexed(internalWidgets) { index, item ->
                    WidgetSelectionRow(
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

                if (initialDays != null && daysLabel != null) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = customDays,
                            onValueChange = { if (it.all { char -> char.isDigit() }) customDays = it },
                            label = { Text(daysLabel) },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // PRZYCISKI ZAPISZ / ZAMKNIJ
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { 
                        onSave(internalWidgets, customDays.toIntOrNull()) 
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
fun WidgetSelectionRow(
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
            Checkbox(
                checked = item.isEnabled,
                onCheckedChange = onCheckedChange
            )
            
            Text(
                text = label,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge
            )

            IconButton(onClick = onMoveUp, enabled = !isFirst) {
                Icon(Icons.Default.KeyboardArrowUp, contentDescription = texts.STATS_MOVE_UP)
            }
            
            IconButton(onClick = onMoveDown, enabled = !isLast) {
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = texts.STATS_MOVE_DOWN)
            }
        }
    }
}
