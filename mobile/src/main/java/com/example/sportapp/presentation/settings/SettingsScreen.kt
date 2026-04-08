package com.example.sportapp.presentation.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.sportapp.LocalMobileTexts
import com.example.sportapp.R

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
    onNavigateToLanguageSelection: () -> Unit
) {
    var state by remember { mutableStateOf(initialState) }
    val scrollState = rememberScrollState()
    val texts = LocalMobileTexts.current

    val isDark = when (state.themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
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
            // 0. Sekcja Wygląd
            SettingsSection(title = texts.SETTINGS_APPEARANCE) {
                Text(text = texts.SETTINGS_THEME, style = MaterialTheme.typography.bodyMedium)
                ThemeSelectionGrid(
                    selectedMode = state.themeMode,
                    onSelect = { state = state.copy(themeMode = it) }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedCard(
                    onClick = onNavigateToLanguageSelection,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ListItem(
                        headlineContent = { Text(texts.SETTINGS_LANGUAGE) },
                        supportingContent = { Text(state.language.label) },
                        leadingContent = { Icon(Icons.Default.Language, null) },
                        trailingContent = { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null) }
                    )
                }
            }

            // 0. Sekcja Profil
            SettingsSection(title = texts.SETTINGS_MY_PROFILE) {
                OutlinedCard(
                    onClick = onNavigateToHealthData,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ListItem(
                        headlineContent = { Text(texts.SETTINGS_HEALTH_DATA) },
                        supportingContent = { Text(texts.SETTINGS_HEALTH_DATA_DESC) },
                        leadingContent = { Icon(Icons.Default.Favorite, null) },
                        trailingContent = { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null) }
                    )
                }
            }

            // 1. Sekcja Aktywności
            SettingsSection(title = texts.NAV_ACTIVITIES) {
                OutlinedCard(
                    onClick = onNavigateToDefinitions,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ListItem(
                        headlineContent = { Text(texts.SETTINGS_DEFINITIONS) },
                        supportingContent = { Text(texts.SETTINGS_DEFINITIONS_DESC) },
                        leadingContent = { Icon(Icons.Default.SettingsAccessibility, null) },
                        trailingContent = { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null) }
                    )
                }
            }

            // 2. Sekcja Widgety na stronie głównej
            SettingsSection(title = texts.SETTINGS_WIDGETS_HOME_TITLE) {
                OutlinedCard(
                    onClick = onNavigateToWidgetSelection,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ListItem(
                        headlineContent = { Text(texts.SETTINGS_WIDGETS_HOME) },
                        supportingContent = { Text(texts.SETTINGS_WIDGETS_HOME_DESC) },
                        leadingContent = { Icon(Icons.Default.Dashboard, null) },
                        trailingContent = { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null) }
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(text = texts.SETTINGS_PERIOD_HOME_DESC, style = MaterialTheme.typography.bodyMedium)
                PeriodSelectionGrid(
                    selectedPeriod = state.period,
                    onSelect = { state = state.copy(period = it) }
                )

                if (state.period == ReportingPeriod.CUSTOM) {
                    OutlinedTextField(
                        value = state.customDays.toString(),
                        onValueChange = { 
                            val days = it.toIntOrNull() ?: 0
                            if (days in 1..35000) {
                                state = state.copy(customDays = days)
                            }
                        },
                        label = { Text(texts.SETTINGS_CUSTOM_DAYS_LABEL) },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                        )
                    )
                }
            }

            // 3. Sekcja Statystyki na zegarku
            SettingsSection(title = texts.SETTINGS_WIDGETS_WATCH) {
                OutlinedCard(
                    onClick = onNavigateToWatchWidgetSelection,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ListItem(
                        headlineContent = { Text(texts.SETTINGS_WIDGETS_WATCH_TITLE) },
                        supportingContent = { Text(texts.SETTINGS_WIDGETS_WATCH_DESC) },
                        leadingContent = { Icon(Icons.Default.Watch, null) },
                        trailingContent = { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null) }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(text = texts.SETTINGS_PERIOD_WATCH_DESC, style = MaterialTheme.typography.bodyMedium)
                PeriodSelectionGrid(
                    selectedPeriod = state.watchStatsPeriod,
                    onSelect = { state = state.copy(watchStatsPeriod = it) }
                )

                if (state.watchStatsPeriod == ReportingPeriod.CUSTOM) {
                    OutlinedTextField(
                        value = state.watchStatsCustomDays.toString(),
                        onValueChange = { 
                            val days = it.toIntOrNull() ?: 0
                            if (days in 1..35000) {
                                state = state.copy(watchStatsCustomDays = days)
                            }
                        },
                        label = { Text(texts.SETTINGS_CUSTOM_DAYS_LABEL) },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                        )
                    )
                }
            }

            // 4. Sekcja Integracja
            SettingsSection(title = texts.SETTINGS_INTEGRATION) {
                ListItem(
                    headlineContent = { Text(texts.SETTINGS_GOOGLE_DRIVE) },
                    supportingContent = { Text(texts.SETTINGS_GOOGLE_DRIVE_DESC) },
                    leadingContent = { Icon(Icons.Default.CloudQueue, null) },
                    trailingContent = { Switch(checked = false, onCheckedChange = null, enabled = false) }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // PRZYCISKI ZAPISZ / ZAMKNIJ
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { onSave(state) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(texts.SETTINGS_SAVE, color = MaterialTheme.colorScheme.onPrimary)
                }
                Button(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(texts.SETTINGS_CLOSE, color = MaterialTheme.colorScheme.onError)
                }
            }

            // Stopka autorska
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = if (isDark) R.drawable.logo_mrf_dark_mode else R.drawable.logo_mrf),
                        contentDescription = "Logo MRF",
                        modifier = Modifier.height(40.dp).width(80.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Image(
                        painter = painterResource(id = if (isDark) R.drawable.logo_emeref_dark_mode else R.drawable.logo_emeref),
                        contentDescription = "Logo Emeref",
                        modifier = Modifier.height(40.dp).width(80.dp)
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun ThemeSelectionGrid(
    selectedMode: ThemeMode,
    onSelect: (ThemeMode) -> Unit
) {
    val texts = LocalMobileTexts.current
    Row(modifier = Modifier.fillMaxWidth()) {
        ThemeOptionChip(texts.SETTINGS_THEME_SYSTEM, ThemeMode.SYSTEM, selectedMode, onSelect, Modifier.weight(1f))
        ThemeOptionChip(texts.SETTINGS_THEME_LIGHT, ThemeMode.LIGHT, selectedMode, onSelect, Modifier.weight(1f))
        ThemeOptionChip(texts.SETTINGS_THEME_DARK, ThemeMode.DARK, selectedMode, onSelect, Modifier.weight(1f))
    }
}

@Composable
fun ThemeOptionChip(
    label: String,
    mode: ThemeMode,
    selectedMode: ThemeMode,
    onSelect: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clickable { onSelect(mode) }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = mode == selectedMode, onClick = { onSelect(mode) })
        Text(text = label, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun PeriodSelectionGrid(
    selectedPeriod: ReportingPeriod,
    onSelect: (ReportingPeriod) -> Unit
) {
    val texts = LocalMobileTexts.current
    Column {
        Row(modifier = Modifier.fillMaxWidth()) {
            PeriodOptionChip(texts.PERIOD_TODAY, ReportingPeriod.TODAY, selectedPeriod, onSelect, Modifier.weight(1f))
            PeriodOptionChip(texts.PERIOD_WEEK, ReportingPeriod.WEEK, selectedPeriod, onSelect, Modifier.weight(1f))
            PeriodOptionChip(texts.PERIOD_MONTH, ReportingPeriod.MONTH, selectedPeriod, onSelect, Modifier.weight(1f))
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            PeriodOptionChip(texts.PERIOD_YEAR, ReportingPeriod.YEAR, selectedPeriod, onSelect, Modifier.weight(1f))
            PeriodOptionChip(texts.PERIOD_CUSTOM, ReportingPeriod.CUSTOM, selectedPeriod, onSelect, Modifier.weight(1f))
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun PeriodOptionChip(
    label: String,
    period: ReportingPeriod,
    selectedPeriod: ReportingPeriod,
    onSelect: (ReportingPeriod) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clickable { onSelect(period) }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = period == selectedPeriod, onClick = { onSelect(period) })
        Text(text = label, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
        content()
    }
}
