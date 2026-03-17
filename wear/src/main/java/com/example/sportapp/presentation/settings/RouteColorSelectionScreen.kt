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
fun RouteColorSelectionScreen(selectedColor: Color, onColorSelected: (Color) -> Unit) {
    val listState = rememberScalingLazyListState()
    val colors = listOf(
        SettingsManager.Orange to "Pomarańczowy",
        Color.White to "Biały",
        Color.Black to "Czarny",
        Color.Red to "Czerwony",
        Color.Cyan to "Niebieski",
        Color.Green to "Zielony",
        Color.Yellow to "Żółty"
    )

    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item { ListHeader { Text("Kolor śladu") } }
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
