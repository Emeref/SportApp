package com.example.sportapp.presentation.settings

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.*
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun MapZoomSelectionScreen(
    currentZoom: Float,
    onZoomSelected: (Float) -> Unit
) {
    val listState = rememberScalingLazyListState()
    // Używamy roundToInt, aby uniknąć problemów z precyzją Float (np. 15.999 -> 15)
    var sliderValue by remember { mutableFloatStateOf(currentZoom.coerceIn(15f, 19f)) }

    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        item {
            ListHeader {
                Text("Poziom przybliżenia")
            }
        }

        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            ) {
                val displayValue = sliderValue.roundToInt()
                Text(
                    text = "$displayValue",
                    style = MaterialTheme.typography.title1,
                    color = MaterialTheme.colors.secondary
                )
                
                Spacer(modifier = Modifier.height(8.dp))

                InlineSlider(
                    value = sliderValue,
                    onValueChange = { 
                        val rounded = it.roundToInt().toFloat()
                        sliderValue = rounded
                        onZoomSelected(rounded)
                    },
                    increaseIcon = { Icon(InlineSliderDefaults.Increase, "Zwiększ") },
                    decreaseIcon = { Icon(InlineSliderDefaults.Decrease, "Zmniejsz") },
                    valueRange = 15f..19f,
                    steps = 3,
                    segmented = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                val label = when (displayValue) {
                    15 -> "Osiedle"
                    16 -> "Aleje"
                    17 -> "Ulice"
                    18 -> "Budynki"
                    19 -> "Maksymalne"
                    else -> "Poziom: $displayValue"
                }
                Text(label, style = MaterialTheme.typography.caption2, color = Color.Gray)
            }
        }
    }
}
