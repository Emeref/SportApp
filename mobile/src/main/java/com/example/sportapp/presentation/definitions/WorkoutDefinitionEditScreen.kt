package com.example.sportapp.presentation.definitions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sportapp.LocalMobileTexts
import com.example.sportapp.data.model.BaseType
import com.example.sportapp.data.model.SensorConfig
import com.example.sportapp.data.model.WorkoutDefinition
import com.example.sportapp.data.model.WorkoutSensor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutDefinitionEditScreen(
    definitionId: Long?,
    onBack: () -> Unit,
    viewModel: WorkoutDefinitionViewModel = hiltViewModel()
) {
    val texts = LocalMobileTexts.current
    val existingDefinition by viewModel.getDefinition(definitionId ?: -1).collectAsState(initial = null)
    
    var name by remember(existingDefinition) { mutableStateOf(existingDefinition?.name ?: "") }
    var iconName by remember(existingDefinition) { mutableStateOf(existingDefinition?.iconName ?: "DirectionsRun") }
    var baseType by remember(existingDefinition) { mutableStateOf(existingDefinition?.baseType ?: BaseType.OTHER) }
    var autoLapDistance by remember(existingDefinition) { mutableStateOf(existingDefinition?.autoLapDistance?.toString() ?: "") }
    
    val sensors = remember(existingDefinition) {
        val initialSensors = existingDefinition?.sensors ?: WorkoutSensor.entries.map { 
            SensorConfig(it.id, isVisible = true, isRecording = true) 
        }
        val processedInitial = initialSensors.map { 
            if (it.sensorId == "map") it.copy(isVisible = false) else it 
        }
        mutableStateListOf<SensorConfig>().apply { 
            clear()
            addAll(processedInitial) 
        }
    }

    var showIconPicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (definitionId == null || definitionId == 0L) texts.DEF_NEW_ACTIVITY else texts.DEF_EDIT_ACTIVITY) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    TextButton(onClick = {
                        val definition = WorkoutDefinition(
                            id = definitionId ?: 0,
                            name = name,
                            iconName = iconName,
                            sensors = sensors.toList(),
                            baseType = baseType,
                            autoLapDistance = autoLapDistance.toDoubleOrNull()
                        )
                        viewModel.saveDefinition(definition)
                        onBack()
                    }) {
                        Text(texts.DEF_SAVE, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(texts.DEF_NAME_LABEL, style = MaterialTheme.typography.labelMedium)
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text("Np. Bieżnia") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )
            }

            item {
                Column {
                    Text(texts.DEF_BASE_TYPE, style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    BaseTypePicker(selectedType = baseType, onTypeSelected = { 
                        baseType = it
                        iconName = suggestIconForBaseType(it)
                        if (name.isEmpty()) {
                            name = getBaseTypeLabel(it, texts)
                        }
                    })
                }
            }

            item {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Text(texts.DEF_ICON, modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
                    Button(
                        onClick = { showIconPicker = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF80CBC4), contentColor = Color.Black),
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Icon(getIconForName(iconName), contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(texts.DEF_SELECT_ICON)
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = autoLapDistance,
                    onValueChange = { autoLapDistance = it },
                    label = { Text(texts.DEF_AUTO_LAP_LABEL) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            item {
                Text(texts.DEF_SENSORS, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = texts.DEF_VISIBILITY,
                        modifier = Modifier.width(80.dp),
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )
                    Text(
                        text = texts.DEF_RECORD,
                        modifier = Modifier.width(80.dp),
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(64.dp))
                }
            }

            itemsIndexed(sensors) { index, sensorConfig ->
                val sensor = WorkoutSensor.entries.find { it.id == sensorConfig.sensorId } ?: return@itemsIndexed
                
                SensorEditRow(
                    label = sensor.label(texts),
                    config = sensorConfig,
                    onConfigChange = { updatedConfig ->
                        var newConfig = updatedConfig
                        
                        // Logic: Visibility implies Recording
                        if (newConfig.isVisible) {
                            newConfig = newConfig.copy(isRecording = true)
                        }
                        
                        // Logic: Location data (map) visibility is always disabled
                        if (newConfig.sensorId == "map") {
                            newConfig = newConfig.copy(isVisible = false)
                        }
                        
                        sensors[index] = newConfig

                        // Logic: Location data (map) auto-recording if dependent sensors are recording
                        val dependentOnLocation = listOf("altitude", "totalAscent", "totalDescent", "speedGps", "distanceGps")
                        val anyRecordingRequiringGps = sensors.any { it.sensorId in dependentOnLocation && it.isRecording }
                        val mapIdx = sensors.indexOfFirst { it.sensorId == "map" }
                        if (mapIdx != -1 && anyRecordingRequiringGps && !sensors[mapIdx].isRecording) {
                            sensors[mapIdx] = sensors[mapIdx].copy(isRecording = true)
                        }
                    },
                    onMoveUp = if (index > 0) { { 
                        val item = sensors.removeAt(index)
                        sensors.add(index - 1, item)
                    } } else null,
                    onMoveDown = if (index < sensors.size - 1) { {
                        val item = sensors.removeAt(index)
                        sensors.add(index + 1, item)
                    } } else null
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(16.dp))
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

@Composable
fun SensorEditRow(
    label: String,
    config: SensorConfig,
    onConfigChange: (SensorConfig) -> Unit,
    onMoveUp: (() -> Unit)?,
    onMoveDown: (() -> Unit)?
) {
    val isMap = config.sensorId == "map"
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label, 
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        
        // Visibility column
        Box(modifier = Modifier.width(80.dp), contentAlignment = Alignment.Center) {
            Checkbox(
                checked = config.isVisible, 
                onCheckedChange = { isVisible ->
                    onConfigChange(config.copy(isVisible = isVisible))
                },
                enabled = !isMap,
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFF80CBC4),
                    uncheckedColor = Color.Gray,
                    checkmarkColor = Color.Black,
                    disabledCheckedColor = Color(0x3380CBC4),
                    disabledUncheckedColor = Color(0x3380CBC4)
                )
            )
        }
        
        // Recording column
        Box(modifier = Modifier.width(80.dp), contentAlignment = Alignment.Center) {
            Checkbox(
                checked = config.isRecording, 
                onCheckedChange = { isRecording ->
                    onConfigChange(config.copy(isRecording = isRecording))
                },
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFF80CBC4),
                    uncheckedColor = Color.Gray,
                    checkmarkColor = Color.Black
                )
            )
        }

        // Arrows column
        Row(modifier = Modifier.width(64.dp), horizontalArrangement = Arrangement.End) {
            IconButton(onClick = onMoveUp ?: {}, enabled = onMoveUp != null, modifier = Modifier.size(32.dp)) {
                Icon(
                    Icons.Default.ArrowUpward, 
                    contentDescription = null, 
                    tint = if (onMoveUp != null) MaterialTheme.colorScheme.onSurface else Color.Transparent
                )
            }
            IconButton(onClick = onMoveDown ?: {}, enabled = onMoveDown != null, modifier = Modifier.size(32.dp)) {
                Icon(
                    Icons.Default.ArrowDownward, 
                    contentDescription = null, 
                    tint = if (onMoveDown != null) MaterialTheme.colorScheme.onSurface else Color.Transparent
                )
            }
        }
    }
}

@Composable
fun BaseTypePicker(selectedType: String, onTypeSelected: (String) -> Unit) {
    val texts = LocalMobileTexts.current
    val types = remember(texts) {
        listOf(
            BaseType.WALKING to texts.DEF_WALKING,
            BaseType.SPEED_WALKING to texts.DEF_SPEED_WALKING,
            BaseType.RUNNING to texts.DEF_RUNNING,
            BaseType.TREADMILL_RUNNING to texts.DEF_TREADMILL_RUNNING,
            BaseType.STAIR_CLIMBING to texts.DEF_STAIR_CLIMBING,
            BaseType.STAIR_CLIMBING_MACHINE to texts.DEF_STAIR_CLIMBING_MACHINE,
            BaseType.CYCLING to texts.DEF_CYCLING,
            BaseType.CYCLING_STATIONARY to texts.DEF_CYCLING_STATIONARY,
            BaseType.MOUNTAIN_BIKING to texts.DEF_MOUNTAIN_BIKING,
            BaseType.ROAD_BIKING to texts.DEF_ROAD_BIKING,
            BaseType.HIKING to texts.DEF_HIKING,
            BaseType.ROCK_CLIMBING to texts.DEF_ROCK_CLIMBING,
            BaseType.BOULDERING to texts.DEF_BOULDERING,
            BaseType.HIIT to texts.DEF_HIIT,
            BaseType.ELLIPTICAL to texts.DEF_ELLIPTICAL,
            BaseType.ROWING_MACHINE to texts.DEF_ROWING_MACHINE,
            BaseType.STRENGTH_TRAINING to texts.DEF_STRENGTH_TRAINING,
            BaseType.CALISTHENICS to texts.DEF_CALISTHENICS,
            BaseType.YOGA to texts.DEF_YOGA,
            BaseType.PILATES to texts.DEF_PILATES,
            BaseType.AEROBICS to texts.DEF_AEROBICS,
            BaseType.DANCING to texts.DEF_DANCING,
            BaseType.SWIMMING_POOL to texts.DEF_SWIMMING_POOL,
            BaseType.SWIMMING_OPEN_WATER to texts.DEF_SWIMMING_OPEN_WATER,
            BaseType.KAYAKING to texts.DEF_KAYAKING,
            BaseType.PADDLE_BOARDING to texts.DEF_PADDLE_BOARDING,
            BaseType.SURFING to texts.DEF_SURFING,
            BaseType.SAILING to texts.DEF_SAILING,
            BaseType.FOOTBALL to texts.DEF_FOOTBALL,
            BaseType.BASKETBALL to texts.DEF_BASKETBALL,
            BaseType.TENNIS to texts.DEF_TENNIS,
            BaseType.SQUASH to texts.DEF_SQUASH,
            BaseType.VOLLEYBALL to texts.DEF_VOLLEYBALL,
            BaseType.GOLF to texts.DEF_GOLF,
            BaseType.MARTIAL_ARTS to texts.DEF_MARTIAL_ARTS,
            BaseType.SKIING to texts.DEF_SKIING,
            BaseType.SNOWBOARDING to texts.DEF_SNOWBOARDING,
            BaseType.ICE_SKATING to texts.DEF_ICE_SKATING,
            BaseType.OTHER to texts.DEF_OTHER
        ).sortedBy { it.second }
    }
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedCard(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = types.find { it.first == selectedType }?.second ?: selectedType,
                    style = MaterialTheme.typography.bodyLarge
                )
                Icon(Icons.Default.ArrowDropDown, null)
            }
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

private fun getBaseTypeLabel(type: String, texts: com.example.sportapp.MobileTexts): String {
    return when (type) {
        BaseType.WALKING -> texts.DEF_WALKING
        BaseType.SPEED_WALKING -> texts.DEF_SPEED_WALKING
        BaseType.RUNNING -> texts.DEF_RUNNING
        BaseType.TREADMILL_RUNNING -> texts.DEF_TREADMILL_RUNNING
        BaseType.STAIR_CLIMBING -> texts.DEF_STAIR_CLIMBING
        BaseType.STAIR_CLIMBING_MACHINE -> texts.DEF_STAIR_CLIMBING_MACHINE
        BaseType.CYCLING -> texts.DEF_CYCLING
        BaseType.CYCLING_STATIONARY -> texts.DEF_CYCLING_STATIONARY
        BaseType.MOUNTAIN_BIKING -> texts.DEF_MOUNTAIN_BIKING
        BaseType.ROAD_BIKING -> texts.DEF_ROAD_BIKING
        BaseType.HIKING -> texts.DEF_HIKING
        BaseType.ROCK_CLIMBING -> texts.DEF_ROCK_CLIMBING
        BaseType.BOULDERING -> texts.DEF_BOULDERING
        BaseType.HIIT -> texts.DEF_HIIT
        BaseType.ELLIPTICAL -> texts.DEF_ELLIPTICAL
        BaseType.ROWING_MACHINE -> texts.DEF_ROWING_MACHINE
        BaseType.STRENGTH_TRAINING -> texts.DEF_STRENGTH_TRAINING
        BaseType.CALISTHENICS -> texts.DEF_CALISTHENICS
        BaseType.YOGA -> texts.DEF_YOGA
        BaseType.PILATES -> texts.DEF_PILATES
        BaseType.AEROBICS -> texts.DEF_AEROBICS
        BaseType.DANCING -> texts.DEF_DANCING
        BaseType.SWIMMING_POOL -> texts.DEF_SWIMMING_POOL
        BaseType.SWIMMING_OPEN_WATER -> texts.DEF_SWIMMING_OPEN_WATER
        BaseType.KAYAKING -> texts.DEF_KAYAKING
        BaseType.PADDLE_BOARDING -> texts.DEF_PADDLE_BOARDING
        BaseType.SURFING -> texts.DEF_SURFING
        BaseType.SAILING -> texts.DEF_SAILING
        BaseType.FOOTBALL -> texts.DEF_FOOTBALL
        BaseType.BASKETBALL -> texts.DEF_BASKETBALL
        BaseType.TENNIS -> texts.DEF_TENNIS
        BaseType.SQUASH -> texts.DEF_SQUASH
        BaseType.VOLLEYBALL -> texts.DEF_VOLLEYBALL
        BaseType.GOLF -> texts.DEF_GOLF
        BaseType.MARTIAL_ARTS -> texts.DEF_MARTIAL_ARTS
        BaseType.SKIING -> texts.DEF_SKIING
        BaseType.SNOWBOARDING -> texts.DEF_SNOWBOARDING
        BaseType.ICE_SKATING -> texts.DEF_ICE_SKATING
        else -> texts.DEF_OTHER
    }
}

private fun suggestIconForBaseType(type: String): String {
    return when (type) {
        BaseType.WALKING, BaseType.SPEED_WALKING -> "DirectionsWalk"
        BaseType.RUNNING, BaseType.TREADMILL_RUNNING -> "DirectionsRun"
        BaseType.CYCLING, BaseType.CYCLING_STATIONARY, BaseType.ROAD_BIKING -> "DirectionsBike"
        BaseType.MOUNTAIN_BIKING -> "Mountain"
        BaseType.HIKING -> "Hiking"
        BaseType.ROCK_CLIMBING, BaseType.BOULDERING -> "Terrain"
        BaseType.SWIMMING_POOL, BaseType.SWIMMING_OPEN_WATER -> "Pool"
        BaseType.YOGA, BaseType.PILATES -> "SelfImprovement"
        BaseType.STRENGTH_TRAINING, BaseType.CALISTHENICS -> "Fitness"
        BaseType.HIIT, BaseType.AEROBICS -> "Timer"
        BaseType.KAYAKING, BaseType.PADDLE_BOARDING -> "Kayaking"
        BaseType.SAILING -> "Sailing"
        BaseType.SURFING -> "Surfing"
        BaseType.FOOTBALL -> "SportsSoccer"
        BaseType.BASKETBALL -> "SportsBasketball"
        BaseType.TENNIS, BaseType.SQUASH -> "SportsTennis"
        BaseType.VOLLEYBALL -> "SportsVolleyball"
        BaseType.GOLF -> "Golf"
        BaseType.SKIING -> "DownhillSkiing"
        BaseType.SNOWBOARDING -> "Snowboarding"
        BaseType.ICE_SKATING -> "IceSkating"
        BaseType.DANCING -> "MusicNote"
        BaseType.ROWING_MACHINE -> "Rowing"
        BaseType.STAIR_CLIMBING, BaseType.STAIR_CLIMBING_MACHINE -> "Stairs"
        else -> "DirectionsRun"
    }
}

@Composable
fun IconPickerSelection(onIconSelected: (String) -> Unit, onDismiss: () -> Unit) {
    val texts = LocalMobileTexts.current
    val icons = remember(texts) {
        listOf(
            "DirectionsRun" to texts.DEF_RUNNING,
            "DirectionsWalk" to texts.DEF_WALKING,
            "DirectionsBike" to texts.DEF_CYCLING,
            "Pool" to texts.DEF_SWIMMING,
            "Mountain" to texts.DEF_HIKING,
            "Hiking" to texts.DEF_HIKING,
            "Fitness" to texts.DEF_GYM,
            "SelfImprovement" to texts.DEF_YOGA,
            "SportsTennis" to texts.DEF_TENNIS,
            "Kayaking" to texts.DEF_KAYAKING,
            "Snowboarding" to texts.DEF_SNOWBOARDING,
            "DownhillSkiing" to texts.DEF_SKIING,
            "Surfing" to texts.DEF_SURFING,
            "IceSkating" to texts.DEF_ICE_SKATING,
            "Golf" to texts.DEF_GOLF,
            "SportsSoccer" to texts.DEF_FOOTBALL,
            "SportsBasketball" to texts.DEF_BASKETBALL,
            "SportsVolleyball" to texts.DEF_VOLLEYBALL,
            "SportsBaseball" to texts.DEF_BASEBALL,
            "Sailing" to texts.DEF_SAILING,
            "Skateboarding" to texts.DEF_SKATEBOARDING,
            "Rowing" to texts.DEF_ROWING_MACHINE,
            "Stairs" to texts.DEF_STAIR_CLIMBING,
            "MusicNote" to texts.DEF_DANCING,
            "SportsMartialArts" to texts.DEF_MARTIAL_ARTS,
            "Sports" to texts.DEF_COMPETITION,
            "Timer" to texts.DEF_STOPWATCH
        ).sortedBy { it.second }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(texts.DEF_SELECT_ICON_TITLE) },
        text = {
            LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) {
                itemsIndexed(icons) { _, item ->
                    val (name, label) = item
                    val icon = getIconForName(name)
                    DropdownMenuItem(
                        text = { 
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(icon, contentDescription = null)
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(label)
                            }
                        },
                        onClick = { onIconSelected(name) }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(texts.SETTINGS_CANCEL) }
        }
    )
}
