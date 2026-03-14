package com.example.sportapp.presentation.menu

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.Text

@Composable
fun ChooseSportScreen(
    navController: NavHostController,
    viewModel: ChooseSportViewModel = hiltViewModel()
) {
    val definitions by viewModel.definitions.collectAsState()
    val listState = rememberScalingLazyListState()

    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item { ListHeader { Text("Wybierz sport") } }
        
        if (definitions.isEmpty()) {
            item {
                Text(
                    text = "Brak definicji aktywności. Zdefiniuj je w aplikacji na telefonie.",
                    modifier = Modifier.padding(16.dp),
                    style = androidx.wear.compose.material.MaterialTheme.typography.body2,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }

        items(definitions) { definition ->
            Chip(
                label = { Text(definition.name) },
                onClick = { navController.navigate("dynamic_workout/${definition.id}") },
                icon = { 
                    Icon(
                        imageVector = getIconForName(definition.iconName), 
                        contentDescription = definition.name 
                    ) 
                },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 2.dp)
            )
        }
    }
}
