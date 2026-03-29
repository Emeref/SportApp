package com.example.sportapp.presentation.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.*
import com.example.sportapp.core.i18n.LocalAppStrings

@Composable
fun ScreenBehaviorSelectionScreen(
    currentBehavior: ScreenBehavior,
    onBehaviorSelected: (ScreenBehavior) -> Unit
) {
    val listState = rememberScalingLazyListState()
    val strings = LocalAppStrings.current
    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState
    ) {
        item { ListHeader { Text(strings.screenBehavior) } }
        item {
            ToggleChip(
                checked = currentBehavior == ScreenBehavior.KEEP_SCREEN_ON,
                onCheckedChange = { if (it) onBehaviorSelected(ScreenBehavior.KEEP_SCREEN_ON) },
                label = { Text(strings.alwaysOn) },
                toggleControl = {
                    RadioButton(selected = currentBehavior == ScreenBehavior.KEEP_SCREEN_ON)
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            ToggleChip(
                checked = currentBehavior == ScreenBehavior.AMBIENT,
                onCheckedChange = { if (it) onBehaviorSelected(ScreenBehavior.AMBIENT) },
                label = { Text(strings.ambientMode) },
                toggleControl = {
                    RadioButton(selected = currentBehavior == ScreenBehavior.AMBIENT)
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            ToggleChip(
                checked = currentBehavior == ScreenBehavior.SYSTEM,
                onCheckedChange = { if (it) onBehaviorSelected(ScreenBehavior.SYSTEM) },
                label = { Text(strings.autoMode) },
                toggleControl = {
                    RadioButton(selected = currentBehavior == ScreenBehavior.SYSTEM)
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
