package com.example.sportapp.presentation.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sportapp.LocalMobileTexts
import com.example.sportapp.data.db.SyncMetadataEntity
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SyncStatusScreen(
    onBack: () -> Unit,
    viewModel: SyncStatusViewModel = hiltViewModel()
) {
    val texts = LocalMobileTexts.current
    val isSyncing by viewModel.isSyncing.collectAsState()
    val lastHealthSync by viewModel.lastHealthSync.collectAsState()
    val lastWorkoutSync by viewModel.lastWorkoutSync.collectAsState()
    val unsyncedCount by viewModel.unsyncedCount.collectAsState()
    val history by viewModel.syncHistory.collectAsState()

    val sdf = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(texts.SYNC_STATUS_TITLE) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
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
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SyncInfoRow(
                        label = texts.SYNC_LAST_HEALTH,
                        value = if (lastHealthSync > 0) sdf.format(Date(lastHealthSync)) else texts.SYNC_NEVER
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    SyncInfoRow(
                        label = texts.SYNC_LAST_WORKOUT,
                        value = if (lastWorkoutSync > 0) sdf.format(Date(lastWorkoutSync)) else texts.SYNC_NEVER
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    SyncInfoRow(
                        label = texts.SYNC_UNSYNCED_COUNT,
                        value = unsyncedCount.toString(),
                        isBadge = unsyncedCount > 0
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = texts.SYNC_HISTORY_TITLE,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(history.sortedByDescending { it.lastSyncTime }) { item ->
                    SyncStatusHistoryItem(item, sdf)
                }
            }
        }
    }
}

@Composable
fun SyncInfoRow(label: String, value: String, isBadge: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        if (isBadge) {
            Surface(
                color = MaterialTheme.colorScheme.error,
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = value,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onError
                )
            }
        } else {
            Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SyncStatusHistoryItem(item: com.example.sportapp.data.db.SyncMetadataEntity, sdf: SimpleDateFormat) {
    val texts = LocalMobileTexts.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = when (item.syncDirection) {
                        "FROM_HC" -> texts.SYNC_TYPE_IMPORT
                        "TO_HC" -> texts.SYNC_TYPE_EXPORT
                        "TO_STRAVA" -> "Strava Export"
                        else -> item.syncDirection
                    },
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                if (item.activityName != null) {
                    Text(item.activityName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    item.startTime?.let {
                        val activitySdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                        Text(activitySdf.format(Date(it)), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    Text(item.recordType, style = MaterialTheme.typography.bodySmall)
                }
            }
            Text(
                text = sdf.format(Date(item.lastSyncTime)),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
