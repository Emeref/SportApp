package com.example.sportapp.presentation.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.RadioButton
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.ToggleChip

@Composable
fun ClockColorSelectionScreen(selectedColor: Color?, onColorSelected: (Color?) -> Unit) {
    val listState = rememberScalingLazyListState()
    val colors = listOf(
        Color.Red to "Czerwony",
        Color.White to "Biały",
        Color.Green to "Zielony",
        Color.Yellow to "Żółty",
        Color.Blue to "Niebieski",
        Color.Black to "Czarny",
        null to "Brak"
    )

    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item { ListHeader { Text("Kolor zegara") } }
        colors.forEach { (color, label) ->
            item {
                ToggleChip(
                    checked = selectedColor == color,
                    onCheckedChange = { if (it) onColorSelected(color) },
                    label = { Text(label) },
                    toggleControl = {
                        RadioButton(selected = selectedColor == color)
                    },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }
    }
}
