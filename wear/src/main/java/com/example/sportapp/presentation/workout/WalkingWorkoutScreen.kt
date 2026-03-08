package com.example.sportapp.presentation.workout

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.ExperimentalWearFoundationApi
import androidx.wear.compose.foundation.pager.HorizontalPager
import androidx.wear.compose.foundation.pager.VerticalPager
import androidx.wear.compose.foundation.pager.rememberPagerState
import androidx.wear.compose.foundation.rememberActiveFocusRequester
import androidx.wear.compose.foundation.rotary.RotaryScrollableDefaults
import androidx.wear.compose.foundation.rotary.rotaryScrollable
import androidx.wear.compose.material.*
import com.example.sportapp.presentation.sensors.*
import com.example.sportapp.presentation.settings.HealthData
import com.google.maps.android.compose.MapType
import kotlin.math.cos
import kotlin.math.sin
import java.util.*

@OptIn(ExperimentalWearFoundationApi::class)
@Composable
fun WalkingWorkoutScreen(
    mapType: MapType, 
    clockColor: Color?, 
    healthData: HealthData,
    onEndWorkout: (List<Pair<String, String>>) -> Unit
) {
    val context = LocalContext.current
    var workoutStatus by remember { mutableStateOf(WorkoutStatus.ACTIVE) }
    val horizontalPagerState = rememberPagerState(initialPage = 1, pageCount = { 2 })
    val startTime = remember { Date() }
    
    // Sensory
    val heartRate = rememberHeartRate()
    val stepCount = rememberStepCount(workoutStatus)
    val distanceState = rememberDistance(workoutStatus)
    val speedKmH = rememberSpeed()
    val workoutTimerState = rememberWorkoutTimer(workoutStatus)
    val altitude = rememberAltitude()

    // Logger dla CSV
    val logger = remember { WorkoutLogger(context, "Spacer", healthData) }

    // Zapis co sekundę
    LaunchedEffect(workoutTimerState.totalSeconds) {
        if (workoutStatus == WorkoutStatus.ACTIVE) {
            logger.logData(
                lat = distanceState.currentLat,
                lon = distanceState.currentLon,
                bpm = heartRate,
                kroki = stepCount,
                gpsDystans = distanceState.totalDistance,
                predkosc = speedKmH,
                wysokosc = altitude
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(state = horizontalPagerState) { hPage ->
            if (hPage == 0) {
                WorkoutControlScreen(
                    status = workoutStatus,
                    onTogglePause = {
                        workoutStatus = if (workoutStatus == WorkoutStatus.ACTIVE) 
                            WorkoutStatus.PAUSED else WorkoutStatus.ACTIVE
                    },
                    onEnd = {
                        val stats = logger.getFinalStats()
                        val distanceKm = distanceState.totalDistance / 1000f
                        
                        // Obliczanie średnich prędkości dla podsumowania
                        val durationHours = workoutTimerState.totalSeconds / 3600.0
                        val distanceStepsMeters = (stepCount * healthData.stepLength / 100.0)
                        val avgSpeedSteps = if (durationHours > 0) (distanceStepsMeters / 1000.0) / durationHours else 0.0
                        val avgSpeedGps = if (durationHours > 0) distanceKm / durationHours else 0.0

                        val summary = listOf(
                            "Czas trwania" to workoutTimerState.formattedTime,
                            "Kroki" to "$stepCount",
                            "Dystans (GPS)" to String.format(Locale.US, "%.2f km", distanceKm),
                            "Średnie tętno" to "${(stats["avgBpm"] as? Double)?.toInt() ?: "--"} BPM",
                            "Przewyższenie +" to String.format(Locale.US, "%.1f m", stats["totalAscent"] as Double)
                        )
                        
                        // Zapis podsumowania do pliku zbiorczego (Punkt 4)
                        SummaryManager.saveSummary(
                            context = context,
                            activityName = "Spacer",
                            startTime = startTime,
                            durationFormatted = workoutTimerState.formattedTime,
                            steps = stepCount,
                            distanceSteps = distanceStepsMeters,
                            distanceGps = distanceState.totalDistance,
                            avgSpeedSteps = avgSpeedSteps,
                            avgSpeedGps = avgSpeedGps,
                            totalAscent = stats["totalAscent"] as Double,
                            totalDescent = stats["totalDescent"] as Double,
                            avgBpm = stats["avgBpm"] as? Double
                        )
                        
                        onEndWorkout(summary)
                    }
                )
            } else {
                val verticalPagerState = rememberPagerState(pageCount = { 2 })
                val focusRequester = rememberActiveFocusRequester()
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
                            0 -> MainDataScreen(
                                heartRate = heartRate,
                                stepCount = stepCount,
                                distanceMeters = distanceState.totalDistance,
                                speedKmH = speedKmH,
                                workoutTimerState = workoutTimerState
                            )
                            1 -> MapScreen(mapType, focusRequester)
                        }
                    }

                    val radius = (screenWidthPx / 2) - 12.dp
                    val angleBetweenDots = 10f
                    val startAngle = 90f + (angleBetweenDots * (2 - 1) / 2f)
                    repeat(2) { index ->
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
        }
        
        if (clockColor != null) {
            Box(modifier = Modifier.fillMaxWidth().wrapContentHeight(), contentAlignment = Alignment.TopCenter) {
                TimeText(timeTextStyle = MaterialTheme.typography.caption1.copy(color = clockColor, fontWeight = FontWeight.Bold))
            }
        }
    }
}
