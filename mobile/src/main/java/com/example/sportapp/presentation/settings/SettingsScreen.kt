package com.example.sportapp.presentation.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.sportapp.LocalMobileTexts
import com.example.sportapp.R
import com.example.sportapp.healthconnect.ConflictResolutionPolicy

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
    onNavigateToSync: () -> Unit
) {
    var state by remember { mutableStateOf(initialState) }
    val scrollState = rememberScrollState()
    val texts = LocalMobileTexts.current

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
                ThemeSelectionGrid(
                    selectedMode = state.themeMode,
                    onSelect = { state = state.copy(themeMode = it) }
                )
                
                SettingsRow(
                    title = texts.SETTINGS_LANGUAGE,
                    icon = Icons.Default.Language,
                    onClick = onNavigateToLanguageSelection
                )
            }

            // Sekcja Synchronizacja
            SettingsSection(title = texts.SETTINGS_SYNC) {
                SettingsRow(
                    title = texts.SETTINGS_HC_TITLE,
                    icon = Icons.Default.Sync,
                    onClick = onNavigateToSync
                )
                
                SettingsRow(
                    title = texts.SETTINGS_STRAVA,
                    subtitle = texts.SETTINGS_STRAVA_DESC,
                    icon = Icons.Default.DirectionsRun,
                    onClick = { /* Placeholder */ }
                )
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
fun ConflictPolicySelector(
    currentPolicy: ConflictResolutionPolicy,
    onPolicySelected: (ConflictResolutionPolicy) -> Unit
) {
    val texts = LocalMobileTexts.current
    val policies = listOf(
        ConflictResolutionPolicy.NEWER_WINS to texts.SYNC_CONFLICT_NEWER,
        ConflictResolutionPolicy.LOCAL_WINS to texts.SYNC_CONFLICT_LOCAL,
        ConflictResolutionPolicy.HC_WINS to texts.SYNC_CONFLICT_HC
    )

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        policies.forEach { (policy, label) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onPolicySelected(policy) }
            ) {
                RadioButton(
                    selected = currentPolicy == policy,
                    onClick = { onPolicySelected(policy) }
                )
                Text(label, style = MaterialTheme.typography.bodyMedium)
            }
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
