package com.example.sportapp.presentation.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.example.sportapp.presentation.components.SportDataRow
import com.example.sportapp.presentation.sensors.rememberHeartRate

@Composable
fun MainDataScreen() {
    val heartRate = rememberHeartRate()

    val listState = rememberScalingLazyListState()
    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.background),
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        item {
            Text(
                text = "DANE TRENINGU",
                modifier = Modifier.padding(bottom = 8.dp),
                style = MaterialTheme.typography.caption2,
                color = Color.Gray
            )
        }
        item { SportDataRow("Kroki", "1250", Color.Green) }
        item { SportDataRow("Dystans", "0.85 km", Color.Cyan) }
        item { 
            SportDataRow(
                label = "Tętno", 
                value = if (heartRate > 0) "${heartRate.toInt()} BPM" else "-- BPM", 
                color = Color.Red, 
                isBold = true
            ) 
        }
    }
}
