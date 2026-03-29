package com.example.sportapp.presentation.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.SelectableChip
import androidx.wear.compose.material.Text
import com.example.sportapp.core.i18n.AppLanguage
import com.example.sportapp.core.i18n.LocalAppStrings

@Composable
fun LanguageSelectionScreen(
    selectedLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit
) {
    val listState = rememberScalingLazyListState()
    val strings = LocalAppStrings.current

    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item { ListHeader { Text(strings.language) } }

        items(AppLanguage.values()) { language ->
            SelectableChip(
                selected = language == selectedLanguage,
                onClick = { onLanguageSelected(language) },
                label = { Text(language.label) },
                secondaryLabel = { Text(language.flag) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            )
        }
    }
}
