package com.example.sportapp.presentation.stats

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sportapp.data.model.WorkoutDefinition
import com.example.sportapp.presentation.definitions.WorkoutDefinitionViewModel
import com.example.sportapp.presentation.definitions.getIconForName

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityDetailSettingsListScreen(
    viewModel: WorkoutDefinitionViewModel,
    onNavigateToEdit: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val definitions by viewModel.definitions.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Wybierz aktywność do modyfikacji") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Powrót")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(definitions) { definition ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToEdit(definition.name) },
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = getIconForName(definition.iconName),
                                contentDescription = null,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = definition.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (definition.isDefault) "Standardowa" else "Niestandardowa",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null)
                    }
                }
            }
        }
    }
}
