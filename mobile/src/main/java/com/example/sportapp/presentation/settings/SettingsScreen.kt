package com.example.sportapp.presentation.settings

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import com.example.sportapp.LocalMobileTexts
import com.example.sportapp.R
import com.example.sportapp.healthconnect.HealthConnectManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    initialState: MobileSettingsState,
    onSave: (MobileSettingsState) -> Unit,
    onCancel: () -> Unit,
    onNavigateToWidgetSelection: () -> Unit,
    onNavigateToWatchWidgetSelection: () -> Unit,
    onNavigateToDefinitions: () -> Unit,
    onNavigateToHealthData: () -> Unit,
    onNavigateToLanguageSelection: () -> Unit,
    onNavigateToExerciseImport: () -> Unit,
    settingsManager: MobileSettingsManager // Added to handle denied count updates
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
                title = { 
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo_apki_biale),
                            contentDescription = null,
                            modifier = Modifier.size(32.dp).padding(end = 8.dp)
                        )
                        Text(texts.SETTINGS_TITLE)
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onCancel
                    ) {
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
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Sekcja Wygląd
            SettingsSection(title = texts.SETTINGS_APPEARANCE) {
                Text(text = texts.SETTINGS_THEME, style = MaterialTheme.typography.bodyMedium)
                ThemeSelectionGrid(
                    selectedMode = state.themeMode,
                    onSelect = { state = state.copy(themeMode = it) }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                SettingsRow(
                    title = texts.SETTINGS_LANGUAGE,
                    icon = Icons.Default.Language,
                    onClick = onNavigateToLanguageSelection
                )
            }

            // Sekcja Health Connect
            SettingsSection(title = "Health Connect") {
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
                                    text = when(hcStatus) {
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
                                title = texts.HC_SYNC_WORKOUTS,
                                icon = Icons.Default.FitnessCenter,
                                onClick = onNavigateToExerciseImport
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(texts.SETTINGS_HC_AUTO_EXPORT, style = MaterialTheme.typography.bodyLarge)
                                    Text(texts.SETTINGS_HC_AUTO_EXPORT_DESC, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = {
                                    healthConnectManager.openHealthConnectSettings()
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(texts.SETTINGS_HC_MANAGE_PERMISSIONS)
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
            }

            // Sekcja Aktywności
            SettingsSection(title = texts.SETTINGS_GENERAL) {
                SettingsRow(
                    title = texts.SETTINGS_HEALTH_DATA,
                    subtitle = texts.SETTINGS_HEALTH_DATA_DESC,
                    icon = Icons.Default.Favorite,
                    onClick = onNavigateToHealthData
                )
                
                SettingsRow(
                    title = texts.SETTINGS_DEFINITIONS,
                    subtitle = texts.SETTINGS_DEFINITIONS_DESC,
                    icon = Icons.AutoMirrored.Filled.List,
                    onClick = onNavigateToDefinitions
                )
                
                SettingsRow(
                    title = texts.SETTINGS_WIDGETS_HOME,
                    subtitle = texts.SETTINGS_WIDGETS_HOME_DESC,
                    icon = Icons.Default.Dashboard,
                    onClick = onNavigateToWidgetSelection
                )

                SettingsRow(
                    title = texts.SETTINGS_WIDGETS_WATCH,
                    subtitle = texts.SETTINGS_WIDGETS_WATCH_DESC,
                    icon = Icons.Default.Watch,
                    onClick = onNavigateToWatchWidgetSelection
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = { onSave(state) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(texts.SETTINGS_SAVE)
            }
            
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(texts.SETTINGS_CANCEL)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        content()
    }
}

@Composable
fun SettingsRow(
    title: String,
    subtitle: String? = null,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1.0f)) {
                Text(text = title, style = MaterialTheme.typography.bodyLarge)
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ThemeSelectionGrid(
    selectedMode: ThemeMode,
    onSelect: (ThemeMode) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ThemeOption(
            modifier = Modifier.weight(1f),
            title = LocalMobileTexts.current.SETTINGS_THEME_LIGHT,
            icon = Icons.Default.LightMode,
            selected = selectedMode == ThemeMode.LIGHT,
            onClick = { onSelect(ThemeMode.LIGHT) }
        )
        ThemeOption(
            modifier = Modifier.weight(1f),
            title = LocalMobileTexts.current.SETTINGS_THEME_DARK,
            icon = Icons.Default.DarkMode,
            selected = selectedMode == ThemeMode.DARK,
            onClick = { onSelect(ThemeMode.DARK) }
        )
        ThemeOption(
            modifier = Modifier.weight(1f),
            title = LocalMobileTexts.current.SETTINGS_THEME_SYSTEM,
            icon = Icons.Default.SettingsSuggest,
            selected = selectedMode == ThemeMode.SYSTEM,
            onClick = { onSelect(ThemeMode.SYSTEM) }
        )
    }
}

@Composable
fun ThemeOption(
    modifier: Modifier = Modifier,
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    OutlinedCard(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.outlinedCardColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        ),
        border = CardDefaults.outlinedCardBorder(enabled = true).let { 
            if (selected) it.copy(brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary)) else it
        }
    ) {
        Column(
            modifier = Modifier.padding(8.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null)
            Text(text = title, style = MaterialTheme.typography.labelSmall)
        }
    }
}
