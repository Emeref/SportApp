package com.example.sportapp.presentation.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sportapp.BuildConfig
import com.example.sportapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    initialState: MobileSettingsState,
    onSave: (MobileSettingsState) -> Unit,
    onCancel: () -> Unit,
    onNavigateToWidgetSelection: () -> Unit,
    onNavigateToWatchWidgetSelection: () -> Unit,
    onNavigateToDefinitions: () -> Unit
) {
    var state by remember { mutableStateOf(initialState) }
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo_apki_biale),
                            contentDescription = null,
                            modifier = Modifier.size(32.dp).padding(end = 8.dp)
                        )
                        Text("Ustawienia")
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onCancel,
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Powrót")
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
            // 1. Sekcja Aktywności
            SettingsSection(title = "Treningi") {
                OutlinedCard(
                    onClick = onNavigateToDefinitions,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ListItem(
                        headlineContent = { Text("Definicja aktywności") },
                        supportingContent = { Text("Zarządzaj listą i czujnikami sportów") },
                        leadingContent = { Icon(Icons.Default.SettingsAccessibility, null) },
                        trailingContent = { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null) }
                    )
                }
            }

            // 2. Sekcja Widgety na stronie głównej
            SettingsSection(title = "Widok ekranu głównego") {
                OutlinedCard(
                    onClick = onNavigateToWidgetSelection,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ListItem(
                        headlineContent = { Text("Widgety na stronie głównej") },
                        supportingContent = { Text("Wybierz i ustaw kolejność") },
                        leadingContent = { Icon(Icons.Default.Dashboard, null) },
                        trailingContent = { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null) }
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(text = "Za jaki okres pokazujemy widgety?", style = MaterialTheme.typography.bodyMedium)
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
                        label = { Text("Liczba dni") },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                        )
                    )
                }
            }

            // 3. Sekcja Statystyki na zegarku
            SettingsSection(title = "Statystyki na zegarku") {
                OutlinedCard(
                    onClick = onNavigateToWatchWidgetSelection,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ListItem(
                        headlineContent = { Text("Pola statystyk") },
                        supportingContent = { Text("Wybierz i ustaw kolejność pól na zegarku") },
                        leadingContent = { Icon(Icons.Default.Watch, null) },
                        trailingContent = { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null) }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "Z jakiego okresu statystyki?", style = MaterialTheme.typography.bodyMedium)
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
                        label = { Text("Liczba dni") },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                        )
                    )
                }
            }

            // 4. Sekcja Integracja
            SettingsSection(title = "Integracja") {
                ListItem(
                    headlineContent = { Text("Google Drive") },
                    supportingContent = { Text("Zapisuj historię i podsumowania (Wkrótce)") },
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
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text("Zapisz", color = Color.White)
                }
                Button(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
                ) {
                    Text("Zamknij", color = Color.White)
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
                        painter = painterResource(id = R.drawable.logo_mrf),
                        contentDescription = "Logo MRF",
                        modifier = Modifier.height(40.dp).width(80.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Image(
                        painter = painterResource(id = R.drawable.logo_emeref),
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
fun PeriodSelectionGrid(
    selectedPeriod: ReportingPeriod,
    onSelect: (ReportingPeriod) -> Unit
) {
    Column {
        Row(modifier = Modifier.fillMaxWidth()) {
            PeriodOptionChip("Dziś", ReportingPeriod.TODAY, selectedPeriod, onSelect, Modifier.weight(1f))
            PeriodOptionChip("Tydzień", ReportingPeriod.WEEK, selectedPeriod, onSelect, Modifier.weight(1f))
            PeriodOptionChip("Miesiąc", ReportingPeriod.MONTH, selectedPeriod, onSelect, Modifier.weight(1f))
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            PeriodOptionChip("Rok", ReportingPeriod.YEAR, selectedPeriod, onSelect, Modifier.weight(1f))
            PeriodOptionChip("Inne", ReportingPeriod.CUSTOM, selectedPeriod, onSelect, Modifier.weight(1f))
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
