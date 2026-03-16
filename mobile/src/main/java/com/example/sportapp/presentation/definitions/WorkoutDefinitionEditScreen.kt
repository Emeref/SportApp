package com.example.sportapp.presentation.definitions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sportapp.data.model.SensorConfig
import com.example.sportapp.data.model.WorkoutDefinition
import com.example.sportapp.data.model.WorkoutSensor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutDefinitionEditScreen(
    viewModel: WorkoutDefinitionViewModel,
    definitionId: Long,
    onNavigateBack: () -> Unit
) {
    val definitions by viewModel.definitions.collectAsState()
    val existingDefinition = definitions.find { it.id == definitionId }

    var name by remember(existingDefinition) { mutableStateOf(existingDefinition?.name ?: "") }
    var iconName by remember(existingDefinition) { mutableStateOf(existingDefinition?.iconName ?: "DirectionsRun") }
    var baseType by remember(existingDefinition) { mutableStateOf(existingDefinition?.baseType ?: "Other") }
    var sensors by remember(existingDefinition) {
        mutableStateOf(
            existingDefinition?.sensors ?: WorkoutSensor.entries.map {
                SensorConfig(it.id, isVisible = false, isRecording = false)
            }
        )
    }

    var showIconPicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (definitionId == 0L) "Nowa aktywność" else "Edytuj aktywność") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Powrót")
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
            OutlinedTextField(
                value = name,
                onValueChange = { newValue ->
                    if (!newValue.contains("\n")) {
                        name = newValue
                    }
                },
                label = { Text("Nazwa aktywności") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = { showIconPicker = true }) {
                        Icon(getIconForName(iconName), contentDescription = "Wybierz ikonę")
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    "Widget w aktywności",
                    style = MaterialTheme.typography.titleSmall, 
                    modifier = Modifier.weight(1f)
                )
                Text(
                    "Widoczność", 
                    style = MaterialTheme.typography.bodySmall, 
                    fontSize = 9.sp, 
                    modifier = Modifier.width(72.dp),
                    textAlign = TextAlign.Center,
                    lineHeight = 10.sp
                )
                Text(
                    "Zapis", 
                    style = MaterialTheme.typography.bodySmall, 
                    fontSize = 9.sp, 
                    modifier = Modifier.width(52.dp),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.width(64.dp))
            }
            
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                itemsIndexed(sensors) { index, sensorConfig ->
                    val sensor = WorkoutSensor.entries.find { it.id == sensorConfig.sensorId }
                    SensorConfigItem(
                        label = sensor?.label ?: sensorConfig.sensorId,
                        config = sensorConfig,
                        onConfigChange = { newConfig ->
                            val newList = sensors.toMutableList()
                            newList[index] = newConfig
                            sensors = newList
                        },
                        onMoveUp = if (index > 0) { {
                            val newList = sensors.toMutableList()
                            val item = newList.removeAt(index)
                            newList.add(index - 1, item)
                            sensors = newList
                        } } else null,
                        onMoveDown = if (index < sensors.size - 1) { {
                            val newList = sensors.toMutableList()
                            val item = newList.removeAt(index)
                            newList.add(index + 1, item)
                            sensors = newList
                        } } else null
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Typ bazowy", style = MaterialTheme.typography.titleMedium)
            BaseTypePicker(selectedType = baseType, onTypeSelected = { baseType = it })

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = {
                        val newDef = WorkoutDefinition(
                            id = definitionId,
                            name = name,
                            iconName = iconName,
                            sensors = sensors,
                            baseType = baseType,
                            isDefault = existingDefinition?.isDefault ?: false
                        )
                        viewModel.saveDefinition(newDef)
                        onNavigateBack()
                    },
                    modifier = Modifier.weight(1f),
                    enabled = name.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)) // Zielony
                ) {
                    Text("Zapisz", color = Color.White)
                }
                Button(
                    onClick = onNavigateBack,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336)) // Czerwony
                ) {
                    Text("Zakończ", color = Color.White)
                }
            }
        }

        if (showIconPicker) {
            IconPickerSelection(
                onIconSelected = {
                    iconName = it
                    showIconPicker = false
                },
                onDismiss = { showIconPicker = false }
            )
        }
    }
}

@Composable
fun SensorConfigItem(
    label: String,
    config: SensorConfig,
    onConfigChange: (SensorConfig) -> Unit,
    onMoveUp: (() -> Unit)?,
    onMoveDown: (() -> Unit)?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
        }
        
        Checkbox(
            checked = config.isVisible,
            onCheckedChange = { 
                onConfigChange(config.copy(isVisible = it, isRecording = if (it) true else config.isRecording))
            },
            modifier = Modifier.width(72.dp)
        )
        
        Checkbox(
            checked = config.isRecording,
            onCheckedChange = { 
                if (!config.isVisible) {
                    onConfigChange(config.copy(isRecording = it))
                }
            },
            enabled = !config.isVisible,
            modifier = Modifier.width(52.dp)
        )

        Row(modifier = Modifier.width(64.dp), horizontalArrangement = Arrangement.End) {
            IconButton(onClick = { onMoveUp?.invoke() }, enabled = onMoveUp != null, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.ArrowUpward, contentDescription = "Góra")
            }
            IconButton(onClick = { onMoveDown?.invoke() }, enabled = onMoveDown != null, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.ArrowDownward, contentDescription = "Dół")
            }
        }
    }
}

@Composable
fun BaseTypePicker(selectedType: String, onTypeSelected: (String) -> Unit) {
    val types = mapOf(
        "Walking" to "Chodzenie",
        "Running" to "Bieganie",
        "Cycling" to "Jazda na rowerze",
        "Hiking" to "Wędrówka",
        "Other" to "Inne"
    )
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedCard(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                types[selectedType] ?: selectedType,
                modifier = Modifier.padding(16.dp)
            )
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            types.forEach { (key, label) ->
                DropdownMenuItem(
                    text = { Text(label) },
                    onClick = {
                        onTypeSelected(key)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun IconPickerSelection(onIconSelected: (String) -> Unit, onDismiss: () -> Unit) {
    val icons = mapOf(
        "DirectionsRun" to (Icons.AutoMirrored.Filled.DirectionsRun to "Bieganie"),
        "DirectionsWalk" to (Icons.AutoMirrored.Filled.DirectionsWalk to "Chodzenie"),
        "DirectionsBike" to (Icons.AutoMirrored.Filled.DirectionsBike to "Rower"),
        "Pool" to (Icons.Default.Pool to "Pływanie"),
        "Mountain" to (Icons.Default.Terrain to "Góry"),
        "Fitness" to (Icons.Default.FitnessCenter to "Siłownia"),
        "SelfImprovement" to (Icons.Default.SelfImprovement to "Joga"),
        "SportsTennis" to (Icons.Default.SportsTennis to "Tenis"),
        "Kayaking" to (Icons.Default.Kayaking to "Kajakarstwo"),
        "Snowboarding" to (Icons.Default.Snowboarding to "Snowboarding"),
        "Surfing" to (Icons.Default.Surfing to "Surfing"),
        "IceSkating" to (Icons.Default.IceSkating to "Łyżwiarstwo"),
        "Golf" to (Icons.Default.GolfCourse to "Golf"),
        "SportsSoccer" to (Icons.Default.SportsSoccer to "Piłka nożna"),
        "SportsBasketball" to (Icons.Default.SportsBasketball to "Koszykówka"),
        "SportsVolleyball" to (Icons.Default.SportsVolleyball to "Siatkówka"),
        "SportsBaseball" to (Icons.Default.SportsBaseball to "Baseball"),
        "Sailing" to (Icons.Default.Sailing to "Żeglarstwo"),
        "Skateboarding" to (Icons.Default.Skateboarding to "Deskorolka")
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Wybierz ikonę") },
        text = {
            LazyColumn {
                icons.forEach { (name, pair) ->
                    item {
                        DropdownMenuItem(
                            text = { 
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(pair.first, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(pair.second)
                                }
                            },
                            onClick = { onIconSelected(name) }
                        )
                    }
                }
            }
        },
        confirmButton = {}
    )
}
