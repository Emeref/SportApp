package com.example.sportapp.presentation.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sportapp.LocalMobileTexts
import com.example.sportapp.healthconnect.model.ExerciseSessionSyncDto
import java.time.Duration
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseImportScreen(
    onNavigateBack: () -> Unit,
    viewModel: ExerciseImportViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val texts = LocalMobileTexts.current

    if (uiState.showConfirmDialog) {
        val selectedCount = uiState.sessions.count { it.isSelected && !it.alreadyImported }
        AlertDialog(
            onDismissRequest = { viewModel.onDismissConfirm() },
            title = { Text(texts.HC_SYNC_CONFIRM_TITLE) },
            text = { Text(texts.hcImportConfirmDesc(selectedCount)) },
            confirmButton = {
                TextButton(onClick = { viewModel.confirmImport() }) {
                    Text(texts.ACTIVITY_OK)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onDismissConfirm() }) {
                    Text(texts.SETTINGS_CANCEL)
                }
            }
        )
    }

    if (uiState.error != null) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissError() },
            title = { Text(texts.HC_SYNC_ERROR) },
            text = { Text(uiState.error!!) },
            confirmButton = {
                TextButton(onClick = { viewModel.dismissError() }) {
                    Text(texts.ACTIVITY_OK)
                }
            }
        )
    }

    if (uiState.isImporting) {
        Dialog(onDismissRequest = {}) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                tonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator()
                    val progressText = if (uiState.totalToImport > 0) {
                        texts.hcImportProgress(uiState.currentImport, uiState.totalToImport)
                    } else {
                        texts.ACTIVITY_IMPORT_PROGRESS
                    }
                    Text(
                        text = progressText,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(texts.HC_IMPORT_TITLE) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        },
        bottomBar = {
            val selectedCount = uiState.sessions.count { it.isSelected && !it.alreadyImported }
            if (selectedCount > 0) {
                Surface(tonalElevation = 8.dp) {
                    Button(
                        onClick = { viewModel.onImportClick() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        enabled = !uiState.isImporting
                    ) {
                        Text(texts.hcImportSelected(selectedCount))
                    }
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.sessions.isEmpty()) {
                Text(
                    text = texts.HC_IMPORT_EMPTY,
                    modifier = Modifier.padding(16.dp).align(Alignment.Center),
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        val importableSessions = uiState.sessions.filter { !it.alreadyImported }
                        if (importableSessions.isNotEmpty()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.toggleSelectAll(!uiState.allSelected) }
                                    .padding(vertical = 8.dp, horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = uiState.allSelected,
                                    onCheckedChange = { viewModel.toggleSelectAll(it) }
                                )
                                Text(
                                    text = texts.HC_IMPORT_SELECT_ALL,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                            HorizontalDivider(modifier = Modifier.padding(bottom = 8.dp))
                        }
                    }

                    items(uiState.sessions) { session ->
                        ExerciseSessionItem(
                            session = session,
                            onToggle = { viewModel.toggleSelection(session.hcSessionId) }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
fun ExerciseSessionItem(
    session: ExerciseSessionSyncDto,
    onToggle: () -> Unit
) {
    val texts = LocalMobileTexts.current
    val dateFormatter = remember { DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm") }
    val startTime = session.startTime.atZone(ZoneId.systemDefault())
    val duration = Duration.between(session.startTime, session.endTime)
    
    val h = duration.seconds / 3600
    val m = (duration.seconds % 3600) / 60
    val s = duration.seconds % 60
    val durationFormatted = if (h > 0) String.format(Locale.US, "%02d:%02d:%02d", h, m, s)
    else String.format(Locale.US, "%02d:%02d", m, s)

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { if (!session.alreadyImported) onToggle() },
        enabled = !session.alreadyImported,
        colors = CardDefaults.cardColors(
            containerColor = if (session.alreadyImported) 
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = session.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (session.alreadyImported) Color.Gray else Color.Unspecified
                )
                Text(
                    text = startTime.format(dateFormatter),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    InfoChip(label = durationFormatted, icon = null)
                    session.distanceMeters?.let {
                        val distKm = it / 1000.0
                        InfoChip(label = String.format(Locale.US, "%.2f km", distKm), icon = null)
                    }
                    session.activeCalories?.let {
                        InfoChip(label = "${it.toInt()} kcal", icon = null)
                    }
                }
            }
            
            if (session.alreadyImported) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = texts.HC_IMPORT_ALREADY_IMPORTED,
                    tint = Color.Gray
                )
            } else {
                Checkbox(
                    checked = session.isSelected,
                    onCheckedChange = { onToggle() }
                )
            }
        }
    }
}

@Composable
fun InfoChip(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector?) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall
        )
    }
}
