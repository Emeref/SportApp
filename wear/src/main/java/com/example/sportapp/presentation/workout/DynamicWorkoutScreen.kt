package com.example.sportapp.presentation.workout

import android.app.Activity
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.foundation.ExperimentalWearFoundationApi
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.foundation.pager.HorizontalPager
import androidx.wear.compose.foundation.pager.rememberPagerState
import androidx.wear.compose.foundation.rotary.RotaryScrollableDefaults
import androidx.wear.compose.foundation.rotary.rotaryScrollable
import androidx.wear.compose.material.*
import com.example.sportapp.data.model.SensorConfig
import com.example.sportapp.data.model.WorkoutSensor
import com.example.sportapp.presentation.components.SportDataRow
import com.example.sportapp.presentation.settings.HealthData
import com.example.sportapp.presentation.settings.ScreenBehavior
import com.google.maps.android.compose.MapType
import kotlinx.coroutines.delay
import java.util.*

@OptIn(ExperimentalWearFoundationApi::class)
@Composable
fun DynamicWorkoutScreen(
    definitionId: Long,
    mapType: MapType,
    clockColor: Color?,
    healthData: HealthData,
    autoCenterDelay: Int,
    showRoute: Boolean,
    routeColor: Color,
    screenBehavior: ScreenBehavior,
    isAmbient: Boolean,
    onEndWorkout: (String, List<Pair<String, String>>) -> Unit,
    viewModel: DynamicWorkoutViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = context as? Activity

    // Lokalny stan wymuszający pełny UI w trybie Ambient po kliknięciu
    var forceActiveUI by remember { mutableStateOf(false) }

    // Resetuj wymuszenie aktywnego UI, gdy system wejdzie w prawdziwy tryb Ambient
    LaunchedEffect(isAmbient) {
        if (isAmbient) {
            forceActiveUI = false
        }
    }

    // Obsługa FLAG_KEEP_SCREEN_ON
    DisposableEffect(screenBehavior) {
        if (screenBehavior == ScreenBehavior.KEEP_SCREEN_ON) {
            activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        onDispose {
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

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
        definitionId = definitionId,
        onEndWorkout = { summary -> onEndWorkout(sportDef.name, summary) }
    )

    // Decyzja o wyświetleniu trybu uproszczonego:
    // 1. Zawsze gdy system jest w trybie isAmbient (ochrona ekranu)
    // 2. Gdy użytkownik wybrał tryb AMBIENT w ustawieniach i NIE kliknął jeszcze w ekran (forceActiveUI == false)
    val shouldShowAmbientUI = isAmbient || (screenBehavior == ScreenBehavior.AMBIENT && !forceActiveUI)

    if (shouldShowAmbientUI) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    // Pozwól na przełączenie w tryb aktywny tylko jeśli nie jesteśmy w systemowym trybie Ambient
                    if (!isAmbient) {
                        forceActiveUI = true
                    }
                }
        ) {
            AmbientWorkoutUI(session, dataSensors)
        }
    } else {
        ActiveWorkoutUI(
            session = session,
            dataSensors = dataSensors,
            hasMap = hasMap,
            mapType = mapType,
            clockColor = clockColor,
            autoCenterDelay = autoCenterDelay,
            showRoute = showRoute,
            routeColor = routeColor
        )
    }
}

@OptIn(ExperimentalWearFoundationApi::class)
@Composable
private fun ActiveWorkoutUI(
    session: WorkoutSessionState,
    dataSensors: List<SensorConfig>,
    hasMap: Boolean,
    mapType: MapType,
    clockColor: Color?,
    autoCenterDelay: Int,
    showRoute: Boolean,
    routeColor: Color
) {
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
                val listState = rememberScalingLazyListState()
                val focusRequester = remember { FocusRequester() }
                
                val isScrollEnabled = dataSensors.size > 4 || hasMap

                ScalingLazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colors.background)
                        .then(if (isScrollEnabled) {
                            Modifier
                                .rotaryScrollable(
                                    behavior = RotaryScrollableDefaults.behavior(
                                        scrollableState = listState,
                                        hapticFeedbackEnabled = true
                                    ),
                                    focusRequester = focusRequester
                                )
                        } else Modifier)
                        .focusRequester(focusRequester),
                    state = listState,
                    userScrollEnabled = isScrollEnabled,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    contentPadding = PaddingValues(horizontal = 0.dp, vertical = 20.dp)
                ) {
                    item {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(top = 10.dp, start = 8.dp, end = 8.dp)) {
                            Text("CZAS AKTYWNOŚCI", style = MaterialTheme.typography.caption2, color = Color.Gray)
                            Text(session.workoutTimerState.formattedTime, style = MaterialTheme.typography.title1, fontSize = 28.sp)
                        }
                    }

                    if (dataSensors.size <= 3) {
                        items(dataSensors.size) { index ->
                            Box(modifier = Modifier.padding(horizontal = 8.dp)) {
                                DynamicSensorDispatcher(dataSensors[index].sensorId, session, false)
                            }
                        }
                    } else {
                        val rows = dataSensors.chunked(2)
                        items(rows.size) { rowIndex ->
                            val rowSensors = rows[rowIndex]
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                rowSensors.forEach { sensor ->
                                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                        DynamicSensorDispatcher(sensor.sensorId, session, false)
                                    }
                                }
                            }
                        }
                    }

                    if (hasMap) {
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("MAPA", style = MaterialTheme.typography.caption2, color = Color.Gray, modifier = Modifier.padding(horizontal = 8.dp))
                        }
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                            ) {
                                MapScreen(
                                    mapType = mapType, 
                                    focusRequester = focusRequester,
                                    lastPoint = session.lastPoint,
                                    allPoints = session.allPoints,
                                    autoCenterDelay = autoCenterDelay,
                                    showRoute = showRoute,
                                    routeColor = routeColor
                                )
                            }
                        }
                    }
                    
                    item { Spacer(modifier = Modifier.height(24.dp)) }
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
private fun AmbientWorkoutUI(session: WorkoutSessionState, dataSensors: List<SensorConfig>) {
    var currentTime by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        while (true) {
            val cal = Calendar.getInstance()
            currentTime = String.format(Locale.getDefault(), "%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))
            delay(1000)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 1. Zegarek (mniejszy od czasu aktywności)
        Text(
            text = currentTime,
            style = MaterialTheme.typography.caption2,
            color = Color.Gray,
            fontWeight = FontWeight.Medium
        )
        
        Spacer(modifier = Modifier.height(4.dp))

        // 2. Czas aktywności
        Text(
            text = session.workoutTimerState.formattedTime,
            style = MaterialTheme.typography.title1,
            fontSize = 32.sp,
            color = Color.White
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 3. Dwa pierwsze sensory w jednej linii
        val firstTwoSensors = dataSensors.take(2)
        if (firstTwoSensors.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                firstTwoSensors.forEach { sensor ->
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        DynamicSensorDispatcher(sensor.sensorId, session, true)
                    }
                }
            }
        }

        // 4. Trzeci sensor wycentrowany pod nimi
        if (dataSensors.size >= 3) {
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                DynamicSensorDispatcher(dataSensors[2].sensorId, session, true)
            }
        }
    }
}

@Composable
fun DynamicSensorDispatcher(
    id: String,
    session: WorkoutSessionState,
    isAmbient: Boolean
) {
    val sensor = WorkoutSensor.entries.find { it.id == id } ?: return
    val label = sensor.label
    val p = session.lastPoint
    val color = if (isAmbient) Color.White else when (id) {
        "bpm" -> Color.Red
        "steps", "stepsMin" -> Color.Green
        "distanceSteps", "distanceGps" -> Color.Cyan
        "speedGps", "speedSteps" -> Color.Yellow
        "altitude", "totalAscent", "totalDescent" -> Color.Magenta
        "calorieMin", "calorieSum" -> Color.LightGray
        else -> Color.White
    }
    
    when (id) {
        "bpm" -> SportDataRow(label, p?.bpm?.let { "${it.toInt()}" } ?: "--", color, !isAmbient)
        "steps" -> SportDataRow(label, p?.steps?.let { "$it" } ?: "0", color)
        "stepsMin" -> SportDataRow(label, p?.stepsMin?.let { String.format(Locale.US, "%.1f", it) } ?: "--", color)
        "distanceSteps" -> SportDataRow(label, p?.distanceSteps?.let { "$it m" } ?: "-- m", color)
        "distanceGps" -> SportDataRow(label, p?.distanceGps?.let { String.format(Locale.US, "%.2f km", it / 1000.0) } ?: "0.00 km", color)
        "speedGps" -> SportDataRow(label, p?.speedGps?.let { String.format(Locale.US, "%.1f km/h", it) } ?: "0.0 km/h", color)
        "speedSteps" -> SportDataRow(label, p?.speedSteps?.let { String.format(Locale.US, "%.1f km/h", it) } ?: "-- km/h", color)
        "altitude" -> SportDataRow(label, p?.altitude?.let { String.format(Locale.US, "%.0f m", it) } ?: "-- m", color)
        "totalAscent" -> SportDataRow(label, p?.totalAscent?.let { String.format(Locale.US, "%.0f m", it) } ?: "-- m", color)
        "totalDescent" -> SportDataRow(label, p?.totalDescent?.let { String.format(Locale.US, "%.0f m", it) } ?: "-- m", color)
        "calorieMin" -> SportDataRow(label, p?.calorieMin?.let { String.format(Locale.US, "%.1f", it) } ?: "--", color)
        "calorieSum" -> SportDataRow(label, p?.calorieSum?.let { String.format(Locale.US, "%.0f kcal", it) } ?: "0 kcal", color)
    }
}
