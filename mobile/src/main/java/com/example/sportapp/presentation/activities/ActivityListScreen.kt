package com.example.sportapp.presentation.activities

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityListScreen(
    viewModel: ActivityListViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (String) -> Unit
) {
    val activities by viewModel.activities.collectAsState()
    var selectedActivityId by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lista aktywności") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Powrót")
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
        ) {
            // Filtry (póki co placeholder)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { }) { Text("Typ aktywności") }
                OutlinedButton(onClick = { }) { Text("Czas") }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tabela (Uproszczona za pomocą LazyColumn i Row)
            Box(modifier = Modifier.weight(1f).horizontalScroll(rememberScrollState())) {
                Column {
                    // Header
                    Row(modifier = Modifier.padding(vertical = 8.dp)) {
                        Text("", modifier = Modifier.width(48.dp))
                        Text("Typ", modifier = Modifier.width(100.dp), style = MaterialTheme.typography.titleSmall)
                        Text("Data", modifier = Modifier.width(150.dp), style = MaterialTheme.typography.titleSmall)
                        Text("Czas", modifier = Modifier.width(100.dp), style = MaterialTheme.typography.titleSmall)
                        Text("Kalorie", modifier = Modifier.width(80.dp), style = MaterialTheme.typography.titleSmall)
                        Text("Dystans (GPS)", modifier = Modifier.width(120.dp), style = MaterialTheme.typography.titleSmall)
                        Text("Dystans (Kroki)", modifier = Modifier.width(120.dp), style = MaterialTheme.typography.titleSmall)
                    }
                    HorizontalDivider()
                    LazyColumn {
                        items(activities) { activity ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedActivityId = activity.id }
                                    .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedActivityId == activity.id,
                                    onClick = { selectedActivityId = activity.id },
                                    modifier = Modifier.width(48.dp)
                                )
                                Text(activity.type, modifier = Modifier.width(100.dp))
                                Text(activity.date, modifier = Modifier.width(150.dp))
                                Text(activity.duration, modifier = Modifier.width(100.dp))
                                Text(activity.calories, modifier = Modifier.width(80.dp))
                                Text(activity.distanceGps, modifier = Modifier.width(120.dp))
                                Text(activity.distanceSteps, modifier = Modifier.width(120.dp))
                            }
                            HorizontalDivider()
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (selectedActivityId != null) {
                Button(
                    onClick = { onNavigateToDetail(selectedActivityId!!) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Pokaż szczegóły")
                }
            }
        }
    }
}
