package com.example.sportapp.presentation.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SportSettingsScreen(
    settingsState: MobileSettingsState,
    onSave: (MobileSettingsState) -> Unit,
    onNavigateBack: () -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var editingSport by remember { mutableStateOf<SportConfig?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Konfiguracja Sportów") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Powrót")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Dodaj sport")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(settingsState.sports) { sport ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(sport.name, style = MaterialTheme.typography.titleMedium)
                            Text(
                                "Aktywne elementy: ${sport.sensors.count { it.isEnabled }}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        IconButton(onClick = { editingSport = sport }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edytuj")
                        }
                        if (sport.id != "default") {
                            IconButton(onClick = {
                                val newSports = settingsState.sports.filter { it.id != sport.id }
                                onSave(settingsState.copy(sports = newSports))
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Usuń")
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        SportEditDialog(
            sport = SportConfig(UUID.randomUUID().toString(), ""),
            onDismiss = { showAddDialog = false },
            onConfirm = { newSport ->
                onSave(settingsState.copy(sports = settingsState.sports + newSport))
                showAddDialog = false
            }
        )
    }

    if (editingSport != null) {
        val currentEditingSport = editingSport!!
        SportEditDialog(
            sport = currentEditingSport,
            onDismiss = { editingSport = null },
            onConfirm = { updatedSport ->
                val newSports = settingsState.sports.map { 
                    if (it.id == updatedSport.id) updatedSport else it 
                }
                onSave(settingsState.copy(sports = newSports))
                editingSport = null
            }
        )
    }
}

@Composable
fun SportEditDialog(
    sport: SportConfig,
    onDismiss: () -> Unit,
    onConfirm: (SportConfig) -> Unit
) {
    var name by remember { mutableStateOf(sport.name) }
    var sensorsState by remember { mutableStateOf(sport.sensors) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (sport.name.isEmpty()) "Dodaj nowy sport" else "Edytuj sport") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nazwa sportu") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Wybierz i ustaw kolejność:", style = MaterialTheme.typography.labelLarge)
                
                sensorsState.forEachIndexed { index, sensor ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Checkbox(
                            checked = sensor.isEnabled,
                            onCheckedChange = { isChecked ->
                                sensorsState = sensorsState.toMutableList().apply {
                                    this[index] = sensor.copy(isEnabled = isChecked)
                                }
                            }
                        )
                        Text(sensor.label, modifier = Modifier.weight(1f))
                        
                        IconButton(
                            enabled = index > 0,
                            onClick = {
                                sensorsState = sensorsState.toMutableList().apply {
                                    val item = removeAt(index)
                                    add(index - 1, item)
                                }
                            }
                        ) {
                            Text("↑")
                        }
                        IconButton(
                            enabled = index < sensorsState.size - 1,
                            onClick = {
                                sensorsState = sensorsState.toMutableList().apply {
                                    val item = removeAt(index)
                                    add(index + 1, item)
                                }
                            }
                        ) {
                            Text("↓")
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(sport.copy(name = name, sensors = sensorsState)) },
                enabled = name.isNotBlank()
            ) {
                Text("Zapisz")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Anuluj")
            }
        }
    )
}
