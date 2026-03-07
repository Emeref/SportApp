package com.example.sportapp.presentation.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.RadioButton
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.ToggleChip
import com.google.maps.android.compose.MapType

@Composable
fun MapTypeSelectionScreen(selectedType: MapType, onTypeSelected: (MapType) -> Unit) {
    val listState = rememberScalingLazyListState()
    val mapTypes = listOf(
        MapType.NORMAL to "Normalna",
        MapType.SATELLITE to "Satelitarna",
        MapType.TERRAIN to "Terenowa",
        MapType.HYBRID to "Hybrydowa"
    )

    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item { ListHeader { Text("Wybierz rodzaj") } }
        mapTypes.forEach { (type, label) ->
            item {
                ToggleChip(
                    checked = selectedType == type,
                    onCheckedChange = { if (it) onTypeSelected(type) },
                    label = { Text(label) },
                    toggleControl = {
                        RadioButton(selected = selectedType == type)
                    },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }
    }
}
