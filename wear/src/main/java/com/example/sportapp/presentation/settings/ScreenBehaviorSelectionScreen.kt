package com.example.sportapp.presentation.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.*
import com.example.sportapp.LocalWearTexts

@Composable
fun ScreenBehaviorSelectionScreen(
    currentBehavior: ScreenBehavior,
    onBehaviorSelected: (ScreenBehavior) -> Unit
) {
    val texts = LocalWearTexts.current
    val listState = rememberScalingLazyListState()
    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState
    ) {
        item { ListHeader { Text(texts.SETTINGS_SCREEN_BEHAVIOR_TITLE) } }
        item {
            ToggleChip(
                checked = currentBehavior == ScreenBehavior.KEEP_SCREEN_ON,
                onCheckedChange = { if (it) onBehaviorSelected(ScreenBehavior.KEEP_SCREEN_ON) },
                label = { Text(texts.SETTINGS_SCREEN_ALWAYS_ON) },
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
                label = { Text(texts.SETTINGS_SCREEN_AMBIENT) },
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
                label = { Text(texts.SETTINGS_SCREEN_AUTO) },
                toggleControl = {
                    RadioButton(selected = currentBehavior == ScreenBehavior.SYSTEM)
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
