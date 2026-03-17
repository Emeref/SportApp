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

@Composable
fun AutoCenterDelaySelectionScreen(selectedDelay: Int, onDelaySelected: (Int) -> Unit) {
    val listState = rememberScalingLazyListState()
    val delays = listOf(3, 5, 10, 15, 30)

    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item { ListHeader { Text("Autocentrowanie") } }
        delays.forEach { delay ->
            item {
                ToggleChip(
                    checked = selectedDelay == delay,
                    onCheckedChange = { if (it) onDelaySelected(delay) },
                    label = { Text("${delay} sekund") },
                    toggleControl = {
                        RadioButton(selected = selectedDelay == delay)
                    },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }
    }
}
