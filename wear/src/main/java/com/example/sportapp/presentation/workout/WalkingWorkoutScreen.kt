package com.example.sportapp.presentation.workout

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.ExperimentalWearFoundationApi
import androidx.wear.compose.foundation.pager.VerticalPager
import androidx.wear.compose.foundation.pager.rememberPagerState
import androidx.wear.compose.foundation.rememberActiveFocusRequester
import androidx.wear.compose.foundation.rotary.RotaryScrollableDefaults
import androidx.wear.compose.foundation.rotary.rotaryScrollable
import androidx.wear.compose.material.*
import com.example.sportapp.presentation.sensors.*
import com.google.maps.android.compose.MapType
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalWearFoundationApi::class)
@Composable
fun WalkingWorkoutScreen(mapType: MapType, clockColor: Color?) {
    val pageCount = 2
    val pagerState = rememberPagerState(pageCount = { pageCount })
    val focusRequester = rememberActiveFocusRequester()
    val configuration = LocalConfiguration.current
    val screenWidthPx = configuration.screenWidthDp.dp

    // SENSORY URUCHOMIONE TUTAJ - DZIAŁAJĄ PRZEZ CAŁY TRENING
    val heartRate = rememberHeartRate()
    val stepCount = rememberStepCount()
    val distanceMeters = rememberDistance()
    val speedKmH = rememberSpeed()
    val workoutTime = rememberWorkoutTimer()

    Box(modifier = Modifier.fillMaxSize()) {
        // Główny Pager
        VerticalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .rotaryScrollable(
                    behavior = RotaryScrollableDefaults.behavior(pagerState),
                    focusRequester = focusRequester
                )
        ) { page ->
            when (page) {
                0 -> MainDataScreen(
                    heartRate = heartRate,
                    stepCount = stepCount,
                    distanceMeters = distanceMeters,
                    speedKmH = speedKmH,
                    workoutTime = workoutTime
                )
                1 -> MapScreen(mapType, focusRequester)
            }
        }

        // INDYKATOR KROPEK PO ŁUKU
        val dotMarginFromEdge = 12.dp
        val radius = (screenWidthPx / 2) - dotMarginFromEdge
        val angleBetweenDots = 10f
        val startAngle = 90f + (angleBetweenDots * (pageCount - 1) / 2f)

        repeat(pageCount) { index ->
            val isSelected = pagerState.currentPage == index
            val size by animateDpAsState(targetValue = if (isSelected) 8.dp else 5.dp, label = "dotSize")
            val color = if (isSelected) Color.White else Color.Gray.copy(alpha = 0.5f)
            
            val currentAngle = startAngle - (index * angleBetweenDots)
            val angleRad = Math.toRadians(currentAngle.toDouble())
            
            val xOffset = (radius.value * cos(angleRad)).dp
            val yOffset = (radius.value * sin(angleRad)).dp

            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(x = xOffset, y = yOffset)
                    .size(size)
                    .clip(CircleShape)
                    .background(color)
            )
        }
        
        // Zegar
        if (clockColor != null) {
            Box(
                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                contentAlignment = Alignment.TopCenter
            ) {
                TimeText(
                    timeTextStyle = MaterialTheme.typography.caption1.copy(
                        color = clockColor,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}
