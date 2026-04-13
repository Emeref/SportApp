package com.example.sportapp.presentation.settings

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import com.example.sportapp.LocalMobileTexts
import com.example.sportapp.healthconnect.HealthConnectManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthConnectSettingsScreen(
    initialState: MobileSettingsState,
    onSave: (MobileSettingsState) -> Unit,
    onCancel: () -> Unit,
    onNavigateToSyncStatus: () -> Unit,
    onNavigateToExerciseImport: () -> Unit,
    settingsManager: MobileSettingsManager
) {
    var state by remember { mutableStateOf(initialState) }
    val scrollState = rememberScrollState()
    val texts = LocalMobileTexts.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val healthConnectManager = remember {
        val client = HealthConnectClient.getOrCreate(context)
        HealthConnectManager(context, client)
    }

    val hcStatus = remember {
        val status = HealthConnectClient.getSdkStatus(context)
        when (status) {
            HealthConnectClient.SDK_AVAILABLE -> "AVAILABLE"
            HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED -> "NOT_INSTALLED"
            else -> "UNAVAILABLE"
        }
    }

    var showPermissionRationale by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        PermissionController.createRequestPermissionResultContract()
    ) { granted ->
        scope.launch {
            if (granted.containsAll(healthConnectManager.writePermissions)) {
                state = state.copy(autoExportToHC = true)
                settingsManager.resetHcDeniedCount()
            } else {
                state = state.copy(autoExportToHC = false)
                settingsManager.incrementHcDeniedCount()
                Toast.makeText(context, texts.HC_EXPORT_PERMISSION_DENIED, Toast.LENGTH_SHORT).show()
            }
        }
    }

    if (showPermissionRationale) {
        AlertDialog(
            onDismissRequest = { showPermissionRationale = false },
            title = { Text(texts.HC_PERMISSIONS_DIALOG_TITLE) },
            text = { Text(texts.HC_PERMISSIONS_DIALOG_DESC) },
            confirmButton = {
                Button(onClick = {
                    showPermissionRationale = false
                    healthConnectManager.openHealthConnectSettings()
                }) {
                    Text(texts.HC_OPEN_SETTINGS)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionRationale = false }) {
                    Text(texts.SETTINGS_CANCEL)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(texts.SETTINGS_HC_TITLE) },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = texts.SETTINGS_CANCEL)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.HealthAndSafety,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(texts.SETTINGS_HC_STATUS, style = MaterialTheme.typography.titleSmall)
                            Text(
                                text = when (hcStatus) {
                                    "AVAILABLE" -> texts.HC_STATUS_AVAILABLE
                                    "NOT_INSTALLED" -> texts.HC_STATUS_NOT_INSTALLED
                                    else -> texts.HC_STATUS_UNAVAILABLE
                                },
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (hcStatus == "AVAILABLE") {
                        SettingsRow(
                            title = texts.SYNC_STATUS_TITLE,
                            icon = Icons.Default.Sync,
                            onClick = onNavigateToSyncStatus
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        SettingsRow(
                            title = texts.HC_SYNC_WORKOUTS,
                            icon = Icons.Default.FitnessCenter,
                            onClick = onNavigateToExerciseImport
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(texts.SYNC_CONFLICT_POLICY, style = MaterialTheme.typography.labelMedium)
                        ConflictPolicySelector(
                            currentPolicy = state.conflictResolutionPolicy,
                            onPolicySelected = { state = state.copy(conflictResolutionPolicy = it) }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(texts.SETTINGS_HC_AUTO_EXPORT, style = MaterialTheme.typography.bodyLarge)
                                Text(
                                    texts.SETTINGS_HC_AUTO_EXPORT_DESC,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = state.autoExportToHC,
                                onCheckedChange = { checked ->
                                    if (checked) {
                                        scope.launch {
                                            if (healthConnectManager.hasPermissions(healthConnectManager.writePermissions)) {
                                                state = state.copy(autoExportToHC = true)
                                            } else {
                                                if (state.hcPermissionsDeniedCount >= 2) {
                                                    showPermissionRationale = true
                                                } else {
                                                    permissionLauncher.launch(healthConnectManager.writePermissions)
                                                }
                                            }
                                        }
                                    } else {
                                        state = state.copy(autoExportToHC = false)
                                    }
                                }
                            )
                        }
                    } else if (hcStatus == "NOT_INSTALLED") {
                        Button(
                            onClick = { healthConnectManager.openHealthConnectInstallPage() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(texts.HC_INSTALL)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { onSave(state) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(texts.SETTINGS_SAVE)
            }
        }
    }
}
