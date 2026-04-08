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
import com.example.sportapp.LocalWearTexts

@Composable
fun ClockColorSelectionScreen(selectedColor: Color?, onColorSelected: (Color?) -> Unit) {
    val texts = LocalWearTexts.current
    val listState = rememberScalingLazyListState()
    val colors = listOf(
        Color.Red to texts.COLOR_RED,
        Color.White to texts.COLOR_WHITE,
        Color.Green to texts.COLOR_GREEN,
        Color.Yellow to texts.COLOR_YELLOW,
        Color.Blue to texts.COLOR_BLUE,
        Color.Black to texts.COLOR_BLACK,
        null to texts.COLOR_NONE
    )

    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item { ListHeader { Text(texts.SETTINGS_CLOCK_COLOR) } }
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
