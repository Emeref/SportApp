package com.example.sportapp.presentation.definitions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sportapp.LocalMobileTexts
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
    val texts = LocalMobileTexts.current
    val definitions by viewModel.definitions.collectAsState()
    val existingDefinition = definitions.find { it.id == definitionId }

    var name by remember(existingDefinition) { mutableStateOf(existingDefinition?.name ?: "") }
    var iconName by remember(existingDefinition) { mutableStateOf(existingDefinition?.iconName ?: "DirectionsRun") }
    var baseType by remember(existingDefinition) { mutableStateOf(existingDefinition?.baseType ?: "Other") }
    var autoLapDistance by remember(existingDefinition) { 
        mutableStateOf(existingDefinition?.autoLapDistance?.toInt()?.toString() ?: "") 
    }
    var sensors by remember(existingDefinition) {
        val allSensorIds = WorkoutSensor.entries.map { it.id }.toSet()
        val initialSensors = if (existingDefinition != null) {
            val filtered = existingDefinition.sensors.filter { it.sensorId in allSensorIds }
            val existingIds = filtered.map { it.sensorId }.toSet()
            val missing = WorkoutSensor.entries.filter { it.id !in existingIds }.map {
                SensorConfig(it.id, isVisible = false, isRecording = false)
            }
            filtered + missing
        } else {
            WorkoutSensor.entries.map {
                SensorConfig(it.id, isVisible = false, isRecording = false)
            }
        }
        mutableStateOf(initialSensors)
    }

    val gpsSensors = listOf(
        WorkoutSensor.SPEED_GPS.id,
        WorkoutSensor.DISTANCE_GPS.id,
        WorkoutSensor.ALTITUDE.id,
        WorkoutSensor.TOTAL_ASCENT.id,
        WorkoutSensor.TOTAL_DESCENT.id
    )
    val gpsRequired = sensors.any { it.sensorId in gpsSensors && (it.isVisible || it.isRecording) }

    var showIconPicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (definitionId == 0L) texts.DEF_NEW_ACTIVITY else texts.DEF_EDIT_ACTIVITY) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
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
            OutlinedTextField(
                value = name,
                onValueChange = { newValue ->
                    if (!newValue.contains("\n")) {
                        name = newValue
                    }
                },
                label = { Text(texts.DEF_NAME_LABEL) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = { showIconPicker = true }) {
                        Icon(getIconForName(iconName), contentDescription = texts.DEF_SELECT_ICON)
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = autoLapDistance,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                        autoLapDistance = newValue
                    }
                },
                label = { Text(texts.DEF_AUTO_LAP_LABEL) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    texts.DEF_WIDGET_IN_ACTIVITY,
                    style = MaterialTheme.typography.titleSmall, 
                    modifier = Modifier.weight(1f)
                )
                Text(
                    texts.DEF_VISIBILITY, 
                    style = MaterialTheme.typography.bodySmall, 
                    fontSize = 9.sp, 
                    modifier = Modifier.width(72.dp),
                    textAlign = TextAlign.Center,
                    lineHeight = 10.sp
                )
                Text(
                    texts.DEF_RECORD, 
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
                    val isMap = sensorConfig.sensorId == WorkoutSensor.MAP.id
                    val sensor = WorkoutSensor.entries.find { it.id == sensorConfig.sensorId }
                    
                    val effectiveConfig = if (isMap) {
                        sensorConfig.copy(
                            isVisible = false,
                            isRecording = if (gpsRequired) true else sensorConfig.isRecording
                        )
                    } else {
                        sensorConfig
                    }

                    SensorConfigItem(
                        label = texts.getSensorLabel(sensorConfig.sensorId),
                        config = effectiveConfig,
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
                        } } else null,
                        enabledVisible = !isMap,
                        enabledRecording = if (isMap) !gpsRequired else !effectiveConfig.isVisible
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(texts.DEF_BASE_TYPE, style = MaterialTheme.typography.titleMedium)
            BaseTypePicker(selectedType = baseType, onTypeSelected = { baseType = it })

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = {
                        val finalSensors = sensors.map {
                            if (it.sensorId == WorkoutSensor.MAP.id) {
                                it.copy(
                                    isVisible = false,
                                    isRecording = if (gpsRequired) true else it.isRecording
                                )
                            } else it
                        }
                        val newDef = WorkoutDefinition(
                            id = definitionId,
                            name = name,
                            iconName = iconName,
                            sensors = finalSensors,
                            baseType = baseType,
                            isDefault = existingDefinition?.isDefault ?: false,
                            autoLapDistance = autoLapDistance.toDoubleOrNull()
                        )
                        viewModel.saveDefinition(newDef)
                        onNavigateBack()
                    },
                    modifier = Modifier.weight(1f),
                    enabled = name.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)) // Zielony
                ) {
                    Text(texts.DEF_SAVE, color = Color.White)
                }
                Button(
                    onClick = onNavigateBack,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336)) // Czerwony
                ) {
                    Text(texts.DEF_FINISH, color = Color.White)
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
    onMoveDown: (() -> Unit)?,
    enabledVisible: Boolean = true,
    enabledRecording: Boolean = !config.isVisible
) {
    val texts = LocalMobileTexts.current
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
            enabled = enabledVisible,
            modifier = Modifier.width(72.dp)
        )
        
        Checkbox(
            checked = config.isRecording,
            onCheckedChange = { 
                onConfigChange(config.copy(isRecording = it))
            },
            enabled = enabledRecording,
            modifier = Modifier.width(52.dp)
        )

        Row(modifier = Modifier.width(64.dp), horizontalArrangement = Arrangement.End) {
            IconButton(onClick = { onMoveUp?.invoke() }, enabled = onMoveUp != null, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.ArrowUpward, contentDescription = texts.DEF_MOVE_UP)
            }
            IconButton(onClick = { onMoveDown?.invoke() }, enabled = onMoveDown != null, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.ArrowDownward, contentDescription = texts.DEF_MOVE_DOWN)
            }
        }
    }
}

@Composable
fun BaseTypePicker(selectedType: String, onTypeSelected: (String) -> Unit) {
    val texts = LocalMobileTexts.current
    val types = mapOf(
        "Walking" to texts.DEF_WALKING,
        "Running" to texts.DEF_RUNNING,
        "Cycling" to texts.DEF_CYCLING,
        "Hiking" to texts.DEF_HIKING,
        "Other" to texts.DEF_OTHER
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
    val texts = LocalMobileTexts.current
    val icons = mapOf(
        "DirectionsRun" to (Icons.AutoMirrored.Filled.DirectionsRun to texts.DEF_RUNNING),
        "DirectionsWalk" to (Icons.AutoMirrored.Filled.DirectionsWalk to texts.DEF_WALKING),
        "DirectionsBike" to (Icons.AutoMirrored.Filled.DirectionsBike to texts.DEF_CYCLING),
        "Pool" to (Icons.Default.Pool to texts.DEF_SWIMMING),
        "Mountain" to (Icons.Default.Terrain to texts.DEF_HIKING),
        "Fitness" to (Icons.Default.FitnessCenter to texts.DEF_GYM),
        "SelfImprovement" to (Icons.Default.SelfImprovement to texts.DEF_YOGA),
        "SportsTennis" to (Icons.Default.SportsTennis to texts.DEF_TENNIS),
        "Kayaking" to (Icons.Default.Kayaking to texts.DEF_KAYAKING),
        "Snowboarding" to (Icons.Default.Snowboarding to texts.DEF_SNOWBOARDING),
        "Surfing" to (Icons.Default.Surfing to texts.DEF_SURFING),
        "IceSkating" to (Icons.Default.IceSkating to texts.DEF_SKATING),
        "Golf" to (Icons.Default.GolfCourse to texts.DEF_GOLF),
        "SportsSoccer" to (Icons.Default.SportsSoccer to texts.DEF_FOOTBALL),
        "SportsBasketball" to (Icons.Default.SportsBasketball to texts.DEF_BASKETBALL),
        "SportsVolleyball" to (Icons.Default.SportsVolleyball to texts.DEF_VOLLEYBALL),
        "SportsBaseball" to (Icons.Default.SportsBaseball to texts.DEF_BASEBALL),
        "Sailing" to (Icons.Default.Sailing to texts.DEF_SAILING),
        "Skateboarding" to (Icons.Default.Skateboarding to texts.DEF_SKATEBOARDING),
        "Sports" to (Icons.Default.EmojiEvents to texts.DEF_COMPETITION),
        "Timer" to (Icons.Default.Timer to texts.DEF_STOPWATCH)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(texts.DEF_SELECT_ICON_TITLE) },
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
