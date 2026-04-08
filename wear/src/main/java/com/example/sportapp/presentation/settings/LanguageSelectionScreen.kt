package com.example.sportapp.presentation.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.*
import com.example.sportapp.LocalWearTexts

@Composable
fun LanguageSelectionScreen(
    currentLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit
) {
    val texts = LocalWearTexts.current
    val listState = rememberScalingLazyListState()
    
    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState
    ) {
        item { ListHeader { Text(texts.SETTINGS_LANGUAGE_SELECTION_TITLE) } }
        
        AppLanguage.values().forEach { language ->
            item {
                ToggleChip(
                    checked = currentLanguage == language,
                    onCheckedChange = { if (it) onLanguageSelected(language) },
                    label = { Text(language.label) },
                    toggleControl = { RadioButton(selected = currentLanguage == language) },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }
    }
}
