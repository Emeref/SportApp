package com.example.sportapp.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToStats: () -> Unit,
    onNavigateToActivityList: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val stats by viewModel.stats.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SportApp") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.triggerSync() }) {
                        Icon(Icons.Default.Sync, contentDescription = "Synchronizuj")
                    }
                },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = "Opcje")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Wyniki ostatni tydzień:",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            )

            // Widgety statystyk
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCard(
                    modifier = Modifier.weight(1f), 
                    label = "Liczba aktywności", 
                    value = stats["count"].toString()
                )
                StatCard(
                    modifier = Modifier.weight(1f), 
                    label = "Spalone kalorie", 
                    value = "${stats["calories"]} kcal"
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            StatCard(
                modifier = Modifier.fillMaxWidth(), 
                label = "Przebyte km (GPS)", 
                value = "${stats["distance"]} km"
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onNavigateToStats,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Statystyki ogólne")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onNavigateToActivityList,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Szczegóły konkretnej aktywności")
            }
        }
    }
}

@Composable
fun StatCard(modifier: Modifier = Modifier, label: String, value: String) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = label, style = MaterialTheme.typography.labelMedium)
            Text(text = value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
    }
}
