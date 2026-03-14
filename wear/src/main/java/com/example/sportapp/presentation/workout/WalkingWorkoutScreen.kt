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
import com.example.sportapp.presentation.settings.HealthData
import com.google.maps.android.compose.MapType
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalWearFoundationApi::class)
@Composable
fun WalkingWorkoutScreen(
    mapType: MapType, 
    clockColor: Color?, 
    healthData: HealthData,
    onEndWorkout: (List<Pair<String, String>>) -> Unit
) {
    // Wspólna logika sesji dla każdego sportu
    val session = rememberWorkoutSession(
        activityName = "Spacer",
        healthData = healthData,
        onEndWorkout = onEndWorkout
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
                                heartRate = session.lastPoint?.bpm?.toFloat() ?: 0f,
                                stepCount = session.lastPoint?.steps ?: 0,
                                distanceMeters = session.lastPoint?.distanceGps?.toFloat() ?: 0f,
                                speedKmH = session.lastPoint?.speedGps?.toFloat() ?: 0f,
                                workoutTimerState = session.workoutTimerState
                            )
                            1 -> MapScreen(mapType, focusRequester)
                        }
                    }

                    // Kropki po łuku
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
