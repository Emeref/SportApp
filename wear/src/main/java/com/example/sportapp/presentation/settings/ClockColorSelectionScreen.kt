package com.example.sportapp.presentation.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.example.sportapp.core.i18n.LocalAppStrings

@Composable
fun ClockColorSelectionScreen(selectedColor: Color?, onColorSelected: (Color?) -> Unit) {
    val listState = rememberScalingLazyListState()
    val strings = LocalAppStrings.current
    
    val colors = remember(strings) {
        listOf(
            Color.Red to strings.colorRed,
            Color.White to strings.colorWhite,
            Color.Green to strings.colorGreen,
            Color.Yellow to strings.colorYellow,
            Color.Blue to strings.colorBlue,
            Color.Black to strings.colorBlack,
            null to strings.colorNone
        )
    }

    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item { ListHeader { Text(strings.clockColor) } }
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
