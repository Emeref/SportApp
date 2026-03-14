package com.example.sportapp.presentation.workout

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.foundation.ExperimentalWearFoundationApi
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.foundation.pager.HorizontalPager
import androidx.wear.compose.foundation.pager.VerticalPager
import androidx.wear.compose.foundation.pager.rememberPagerState
import androidx.wear.compose.foundation.rotary.RotaryScrollableDefaults
import androidx.wear.compose.foundation.rotary.rotaryScrollable
import androidx.wear.compose.material.*
import com.example.sportapp.data.model.WorkoutSensor
import com.example.sportapp.presentation.components.SportDataRow
import com.example.sportapp.presentation.settings.HealthData
import com.google.maps.android.compose.MapType
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalWearFoundationApi::class)
@Composable
fun DynamicWorkoutScreen(
    definitionId: Long,
    mapType: MapType,
    clockColor: Color?,
    healthData: HealthData,
    onEndWorkout: (String, List<Pair<String, String>>) -> Unit,
    viewModel: DynamicWorkoutViewModel = hiltViewModel()
) {
    val definition by viewModel.definition.collectAsState()
    
    LaunchedEffect(definitionId) {
        viewModel.loadDefinition(definitionId)
    }

    if (definition == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val sportDef = definition!!
    val visibleSensors = sportDef.sensors.filter { it.isVisible }
    val hasMap = visibleSensors.any { it.sensorId == WorkoutSensor.MAP.id }
    val dataSensors = visibleSensors.filter { it.sensorId != WorkoutSensor.MAP.id }

    val session = rememberWorkoutSession(
        activityName = sportDef.name,
        healthData = healthData,
        onEndWorkout = { summary -> onEndWorkout(sportDef.name, summary) }
    )

    val horizontalPagerState = rememberPagerState(initialPage = 1, pageCount = { 2 })

    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(state = horizontalPagerState) { hPage ->
            if (hPage == 0) {
                WorkoutControls(
                    status = session.status,
                    onTogglePause = session.togglePause,
                    onEnd = session.endWorkout
                )
            } else {
                val verticalPageCount = if (hasMap) 2 else 1
                val verticalPagerState = rememberPagerState(pageCount = { verticalPageCount })
                val focusRequester = remember { FocusRequester() }
                val configuration = LocalConfiguration.current
                val screenWidthPx = configuration.screenWidthDp.dp

                Box(modifier = Modifier.fillMaxSize()) {
                    VerticalPager(
                        state = verticalPagerState,
                        modifier = Modifier
                            .fillMaxSize()
                            .rotaryScrollable(
                                behavior = RotaryScrollableDefaults.behavior(verticalPagerState),
                                focusRequester = focusRequester
                            )
                    ) { vPage ->
                        when (vPage) {
                            0 -> DynamicDataScreen(
                                sensors = dataSensors,
                                session = session
                            )
                            1 -> if (hasMap) MapScreen(mapType, focusRequester)
                        }
                    }

                    if (verticalPageCount > 1) {
                        val radius = (screenWidthPx / 2) - 12.dp
                        val angleBetweenDots = 10f
                        val startAngle = 90f + (angleBetweenDots * (verticalPageCount - 1) / 2f)
                        repeat(verticalPageCount) { index ->
                            val isSelected = verticalPagerState.currentPage == index
                            val size by animateDpAsState(targetValue = if (isSelected) 8.dp else 5.dp, label = "dotSize")
                            val color = if (isSelected) Color.White else Color.Gray.copy(alpha = 0.5f)
                            val angleRad = Math.toRadians((startAngle - (index * angleBetweenDots)).toDouble())
                            Box(
                                modifier = Modifier.align(Alignment.Center)
                                    .offset(x = (radius.value * cos(angleRad)).dp, y = (radius.value * sin(angleRad)).dp)
                                    .size(size).clip(CircleShape).background(color)
                            )
                        }
                    }
                }
                
                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }
            }
        }
        
        if (clockColor != null) {
            Box(modifier = Modifier.fillMaxWidth().wrapContentHeight(), contentAlignment = Alignment.TopCenter) {
                TimeText(timeTextStyle = MaterialTheme.typography.caption1.copy(color = clockColor, fontWeight = FontWeight.Bold))
            }
        }
    }
}

@Composable
fun DynamicDataScreen(
    sensors: List<com.example.sportapp.data.model.SensorConfig>,
    session: WorkoutSessionState
) {
    val listState = rememberScalingLazyListState()
    
    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.background),
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(top = 16.dp)) {
                Text("CZAS AKTYWNOŚCI", style = MaterialTheme.typography.caption2, color = Color.Gray)
                Text(session.workoutTimerState.formattedTime, style = MaterialTheme.typography.title1, fontSize = 28.sp)
            }
        }

        if (sensors.size <= 3) {
            items(sensors.size) { index ->
                DynamicSensorDispatcher(sensors[index].sensorId, session)
            }
        } else {
            val rows = sensors.chunked(2)
            items(rows.size) { rowIndex ->
                val rowSensors = rows[rowIndex]
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    rowSensors.forEach { sensor ->
                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                            DynamicSensorDispatcher(sensor.sensorId, session)
                        }
                    }
                }
            }
        }
        
        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
fun DynamicSensorDispatcher(
    id: String,
    session: WorkoutSessionState
) {
    val sensor = com.example.sportapp.data.model.WorkoutSensor.entries.find { it.id == id } ?: return
    val label = sensor.label
    val p = session.lastPoint
    
    when (id) {
        "bpm" -> SportDataRow(label, p?.bpm?.let { "$it" } ?: "--", Color.Red, true)
        "avgBpm" -> SportDataRow(label, p?.avgBpm?.let { String.format(Locale.US, "%.0f", it) } ?: "--", Color.Red)
        "steps" -> SportDataRow(label, p?.steps?.let { "$it" } ?: "0", Color.Green)
        "stepsMin" -> SportDataRow(label, p?.stepsMin?.let { String.format(Locale.US, "%.1f", it) } ?: "--", Color.Green)
        "distanceSteps" -> SportDataRow(label, p?.distanceSteps?.let { "$it m" } ?: "-- m", Color.Cyan)
        "distanceGps" -> SportDataRow(label, p?.distanceGps?.let { String.format(Locale.US, "%.2f km", it / 1000.0) } ?: "0.00 km", Color.Cyan)
        "speedGps" -> SportDataRow(label, p?.speedGps?.let { String.format(Locale.US, "%.1f km/h", it) } ?: "0.0 km/h", Color.Yellow)
        "speedSteps" -> SportDataRow(label, p?.speedSteps?.let { String.format(Locale.US, "%.1f km/h", it) } ?: "-- km/h", Color.Yellow)
        "altitude" -> SportDataRow(label, p?.altitude?.let { String.format(Locale.US, "%.0f m", it) } ?: "-- m", Color.Magenta)
        "totalAscent" -> SportDataRow(label, p?.totalAscent?.let { String.format(Locale.US, "%.0f m", it) } ?: "-- m", Color.Magenta)
        "totalDescent" -> SportDataRow(label, p?.totalDescent?.let { String.format(Locale.US, "%.0f m", it) } ?: "-- m", Color.Magenta)
        "calorieMin" -> SportDataRow(label, p?.calorieMin?.let { String.format(Locale.US, "%.1f", it) } ?: "--", Color.LightGray)
        "calorieSum" -> SportDataRow(label, p?.calorieSum?.let { String.format(Locale.US, "%.0f kcal", it) } ?: "0 kcal", Color.LightGray)
    }
}
