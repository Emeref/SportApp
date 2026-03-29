package com.example.sportapp.presentation.definitions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sportapp.core.i18n.LocalAppStrings
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
    val strings = LocalAppStrings.current

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
                title = { Text(if (definitionId == 0L) strings.addNewActivity else strings.editActivity) },
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
            OutlinedTextField(
                value = name,
                onValueChange = { newValue ->
                    if (!newValue.contains("\n")) {
                        name = newValue
                    }
                },
                label = { Text(strings.activityNameLabel) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = { showIconPicker = true }) {
                        Icon(getIconForName(iconName), contentDescription = strings.chooseIcon)
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
                label = { Text(strings.autoLapLabel) },
                modifier = Modifier.fillMaxWidth(), singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    strings.widgetInActivity,
                    style = MaterialTheme.typography.titleSmall, 
                    modifier = Modifier.weight(1f)
                )
                Text(
                    strings.visibilityLabel,
                    style = MaterialTheme.typography.bodySmall, 
                    fontSize = 9.sp, 
                    modifier = Modifier.width(72.dp),
                    textAlign = TextAlign.Center,
                    lineHeight = 10.sp
                )
                Text(
                    strings.saveLabel, 
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
                        label = sensor?.getLabel(strings) ?: sensorConfig.sensorId,
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

            Text(strings.baseType, style = MaterialTheme.typography.titleMedium)
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
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text(strings.save, color = Color.White)
                }
                Button(
                    onClick = onNavigateBack,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
                ) {
                    Text(strings.finish, color = Color.White)
                }
            }
        }

        if (showIconPicker) {
            IconPickerSelection(
                onIconSelected = { selectedIcon ->
                    iconName = selectedIcon
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
    val strings = LocalAppStrings.current
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
                Icon(Icons.Default.KeyboardArrowUp, contentDescription = strings.moveUp)
            }
            IconButton(onClick = { onMoveDown?.invoke() }, enabled = onMoveDown != null, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = strings.moveDown)
            }
        }
    }
}

@Composable
fun BaseTypePicker(selectedType: String, onTypeSelected: (String) -> Unit) {
    val types = listOf("Running", "Walking", "Cycling", "Swimming", "Other")
    val strings = LocalAppStrings.current
    val labels = mapOf(
        "Running" to strings.running,
        "Walking" to strings.walking,
        "Cycling" to strings.cycling,
        "Swimming" to strings.swimming,
        "Other" to strings.custom
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        types.forEach { type ->
            FilterChip(
                selected = selectedType == type,
                onClick = { onTypeSelected(type) },
                label = { Text(labels[type] ?: type) }
            )
        }
    }
}

@Composable
fun IconPickerSelection(onIconSelected: (String) -> Unit, onDismiss: () -> Unit) {
    val icons = listOf("DirectionsRun", "DirectionsWalk", "DirectionsBike", "Pool", "FitnessCenter", "SelfImprovement", "Timer")
    val strings = LocalAppStrings.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(strings.chooseIcon) },
        text = {
            LazyColumn(modifier = Modifier.fillMaxWidth().height(300.dp)) {
                items(icons.chunked(3)) { row ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        row.forEach { iconName ->
                            IconButton(onClick = { onIconSelected(iconName) }) {
                                Icon(getIconForName(iconName), contentDescription = null, modifier = Modifier.size(32.dp))
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(strings.cancel) }
        }
    )
}
