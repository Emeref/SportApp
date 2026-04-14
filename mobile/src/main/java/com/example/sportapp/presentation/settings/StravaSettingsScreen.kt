package com.example.sportapp.presentation.settings

import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sportapp.LocalMobileTexts
import com.example.sportapp.data.strava.StravaStorage
import kotlinx.coroutines.launch
import com.example.sportapp.BuildConfig
import com.example.sportapp.data.db.SyncMetadataDao
import com.example.sportapp.data.db.SyncMetadataEntity
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StravaSettingsScreen(
    stravaStorage: StravaStorage,
    settingsManager: MobileSettingsManager,
    syncMetadataDao: SyncMetadataDao,
    onBack: () -> Unit
) {
    val texts = LocalMobileTexts.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isConnected by stravaStorage.isConnected.collectAsState(initial = false)
    val athleteName by stravaStorage.athleteName.collectAsState(initial = null)
    
    val settingsState by settingsManager.settingsFlow.collectAsState(initial = MobileSettingsState())
    val syncHistory by syncMetadataDao.getStravaSyncHistoryFlow().collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(texts.STRAVA_TITLE) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Sync,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isConnected) texts.STRAVA_CONNECTED else texts.STRAVA_NOT_CONNECTED,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        athleteName?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            if (!isConnected) {
                item {
                    Button(
                        onClick = {
                            val clientId = BuildConfig.STRAVA_CLIENT_ID
                            val redirectUri = "sportapp://strava"
                            
                            val authUrl = Uri.parse("https://www.strava.com/oauth/authorize")
                                .buildUpon()
                                .appendQueryParameter("client_id", clientId)
                                .appendQueryParameter("redirect_uri", redirectUri)
                                .appendQueryParameter("response_type", "code")
                                .appendQueryParameter("approval_prompt", "auto")
                                .appendQueryParameter("scope", "activity:write,activity:read_all")
                                .build()
                            
                            val intent = CustomTabsIntent.Builder().build()
                            intent.launchUrl(context, authUrl)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(texts.STRAVA_CONNECT)
                    }
                }
            } else {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = texts.SETTINGS_STRAVA_AUTO_EXPORT,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = texts.SETTINGS_STRAVA_AUTO_EXPORT_DESC,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = settingsState.autoExportToStrava,
                                onCheckedChange = { checked ->
                                    scope.launch {
                                        settingsManager.saveSettings(settingsState.copy(autoExportToStrava = checked))
                                    }
                                }
                            )
                        }
                    }
                }

                item {
                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                stravaStorage.clearTokens()
                                settingsManager.saveSettings(settingsState.copy(autoExportToStrava = false))
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text(texts.STRAVA_DISCONNECT)
                    }
                }
            }

            item {
                Text(
                    text = texts.SETTINGS_STRAVA_DESC,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (syncHistory.isNotEmpty()) {
                item {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    Text(
                        text = texts.STRAVA_SYNC_LOG,
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.fillMaxWidth(),
                        fontWeight = FontWeight.Bold
                    )
                }

                items(syncHistory) { entry ->
                    SyncLogItem(entry)
                }
            }
        }
    }
}

@Composable
fun SyncLogItem(entry: SyncMetadataEntity) {
    val dateTimeFormat = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }
    val timeFormat = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }
    
    val workoutStartTime = entry.startTime?.let { dateTimeFormat.format(Date(it)) } ?: "???"
    val syncTime = timeFormat.format(Date(entry.lastSyncTime))
    val isSuccess = entry.stravaSyncStatus == "SUCCESS"

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isSuccess) Icons.Default.CheckCircle else Icons.Default.Error,
                contentDescription = null,
                tint = if (isSuccess) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.activityName ?: "Activity",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = workoutStartTime,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = syncTime,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
