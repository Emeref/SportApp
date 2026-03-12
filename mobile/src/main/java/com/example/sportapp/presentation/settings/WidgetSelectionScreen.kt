package com.example.sportapp.presentation.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun WidgetSelectionScreen(
    widgets: List<WidgetItem>,
    title: String = "Widgety na stronie głównej",
    onSave: (List<WidgetItem>) -> Unit,
    onCancel: () -> Unit
) {
    // Używamy remember(widgets), aby zaktualizować listę, gdy dane zostaną załadowane z DataStore
    var internalWidgets by remember(widgets) { mutableStateOf(widgets) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
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
        }

        Spacer(modifier = Modifier.height(16.dp))

        // PRZYCISKI ZAPISZ / ZAMKNIJ
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = { onSave(internalWidgets) },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text("Zapisz", color = Color.White)
            }
            Button(
                onClick = onCancel,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
            ) {
                Text("Zamknij", color = Color.White)
            }
        }
    }
}

@Composable
fun WidgetSelectionRow(
    item: WidgetItem,
    isFirst: Boolean,
    isLast: Boolean,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onCheckedChange: (Boolean) -> Unit
) {
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
                text = item.label,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge
            )

            IconButton(onClick = onMoveUp, enabled = !isFirst) {
                Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Przesuń w górę")
            }
            
            IconButton(onClick = onMoveDown, enabled = !isLast) {
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Przesuń w dół")
            }
        }
    }
}
