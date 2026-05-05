package com.example.sportapp.presentation.settings

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.sportapp.LocalMobileTexts
import androidx.hilt.navigation.compose.hiltViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthDataScreen(
    initialData: HealthData,
    onSave: (HealthData) -> Unit,
    onCancel: () -> Unit,
    viewModel: HealthDataViewModel = hiltViewModel()
) {
    val texts = LocalMobileTexts.current
    var data by remember(initialData) { mutableStateOf(initialData) }

    // Helper for formatting decimals without unnecessary .0
    fun formatDecimal(value: Double): String = if (value == value.toInt().toDouble()) value.toInt().toString() else value.toString()
    
    // Helper to validate decimal input (digits and at most one dot or comma)
    fun isValidDecimalInput(input: String): Boolean = 
        input.count { it == '.' || it == ',' } <= 1 && input.all { it.isDigit() || it == '.' || it == ',' }

    var ageText by remember(initialData) { mutableStateOf(data.age.toString()) }
    var weightText by remember(initialData) { mutableStateOf(formatDecimal(data.weight)) }
    var heightText by remember(initialData) { mutableStateOf(formatDecimal(data.height)) }
    var restingHRText by remember(initialData) { mutableStateOf(data.restingHR.toString()) }
    var maxHRText by remember(initialData) { mutableStateOf(data.maxHR.toString()) }
    var vo2MaxText by remember(initialData) { mutableStateOf(data.vo2Max?.let { formatDecimal(it) } ?: "") }
    var stepLengthText by remember(initialData) { mutableStateOf(data.stepLength.toString()) }

    val scrollState = rememberScrollState()
    val uiState by viewModel.uiState.collectAsState()

    val permissionsLauncher = rememberLauncherForActivityResult(
        contract = androidx.health.connect.client.PermissionController.createRequestPermissionResultContract()
    ) { granted ->
        if (granted.isNotEmpty()) {
            viewModel.onSyncClick { /* Już sprawdzamy uprawnienia wewnątrz */ }
        }
    }

    if (uiState.showFieldSelectionDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onCancelFieldSelection() },
            title = { Text(texts.HC_IMPORT_SELECT_FIELDS_TITLE) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    uiState.availableFields.forEach { field ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.toggleField(field) }
                                .padding(vertical = 4.dp)
                        ) {
                            Checkbox(
                                checked = uiState.selectedFields.contains(field),
                                onCheckedChange = { viewModel.toggleField(field) }
                            )
                            Text(
                                text = when (field) {
                                    HealthField.WEIGHT -> texts.HEALTH_WEIGHT
                                    HealthField.HEIGHT -> texts.HEALTH_HEIGHT
                                    HealthField.RESTING_HR -> texts.HEALTH_RESTING_HR
                                    HealthField.MAX_HR -> texts.HEALTH_MAX_HR
                                    HealthField.VO2_MAX -> texts.HEALTH_VO2_MAX
                                },
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.onConfirmFieldSelection() },
                    enabled = uiState.selectedFields.isNotEmpty()
                ) {
                    Text(texts.ACTIVITY_OK)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onCancelFieldSelection() }) {
                    Text(texts.SETTINGS_CANCEL)
                }
            }
        )
    }

    if (uiState.showPreviewDialog && uiState.previewData != null) {
        AlertDialog(
            onDismissRequest = { viewModel.onDismissPreview() },
            title = { Text(texts.HC_SYNC_CONFIRM_TITLE) },
            text = {
                val preview = uiState.previewData!!
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        texts.hcSyncPreview(
                            weight = preview.weight?.let { String.format(Locale.US, "%.2f", it) },
                            height = preview.height?.let { String.format(Locale.US, "%.1f", it) },
                            vo2max = preview.vo2Max?.let { String.format(Locale.US, "%.2f", it) }
                        )
                    )
                    if (preview.restingHR != null) {
                        Text("${texts.HEALTH_RESTING_HR}: ${preview.restingHR} bpm")
                    }
                    if (preview.maxHR != null) {
                        Text("${texts.HEALTH_MAX_HR}: ${preview.maxHR} bpm")
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(texts.HC_SYNC_CONFIRM_DESC, style = MaterialTheme.typography.bodySmall)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onConfirmSync(data) { updated ->
                        data = updated
                        // Update string states with new synced values
                        ageText = updated.age.toString()
                        weightText = formatDecimal(updated.weight)
                        heightText = formatDecimal(updated.height)
                        restingHRText = updated.restingHR.toString()
                        maxHRText = updated.maxHR.toString()
                        vo2MaxText = updated.vo2Max?.let { formatDecimal(it) } ?: ""
                        stepLengthText = updated.stepLength.toString()
                    }
                }) {
                    Text(texts.ACTIVITY_OK)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onDismissPreview() }) {
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(texts.HEALTH_TITLE) },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = texts.SETTINGS_CLOSE)
                    }
                },
                actions = {
                    if (uiState.isAvailable) {
                        IconButton(
                            onClick = {
                                viewModel.onSyncClick { permissions ->
                                    permissionsLauncher.launch(permissions)
                                }
                            },
                            enabled = !uiState.isLoading
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                            } else {
                                Icon(Icons.Default.Sync, contentDescription = texts.HC_SYNC_HEALTH_DATA)
                            }
                        }
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
            if (uiState.isAvailable) {
                OutlinedButton(
                    onClick = {
                        viewModel.onSyncClick { permissions ->
                            permissionsLauncher.launch(permissions)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Default.Sync, contentDescription = null)
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(texts.HC_SYNC_HEALTH_DATA)
                }
            }

            Text(texts.HEALTH_GENDER, style = MaterialTheme.typography.titleMedium)
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = data.gender == Gender.MALE,
                    onClick = { data = data.copy(gender = Gender.MALE) }
                )
                Text(texts.HEALTH_GENDER_MALE, modifier = Modifier.padding(end = 16.dp))
                RadioButton(
                    selected = data.gender == Gender.FEMALE,
                    onClick = { data = data.copy(gender = Gender.FEMALE) }
                )
                Text(texts.HEALTH_GENDER_FEMALE)
            }

            OutlinedTextField(
                value = ageText,
                onValueChange = { if (it.all { c -> c.isDigit() }) ageText = it },
                label = { Text(texts.HEALTH_AGE) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = weightText,
                onValueChange = { if (isValidDecimalInput(it)) weightText = it },
                label = { Text(texts.HEALTH_WEIGHT_KG) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = heightText,
                onValueChange = { if (isValidDecimalInput(it)) heightText = it },
                label = { Text(texts.HEALTH_HEIGHT_CM) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = restingHRText,
                onValueChange = { if (it.all { c -> c.isDigit() }) restingHRText = it },
                label = { Text(texts.HEALTH_RESTING_HR) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = maxHRText,
                onValueChange = { if (it.all { c -> c.isDigit() }) maxHRText = it },
                label = { Text(texts.HEALTH_MAX_HR) },
                supportingText = { Text(texts.HEALTH_MAX_HR_DESC) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = vo2MaxText,
                onValueChange = { if (isValidDecimalInput(it)) vo2MaxText = it },
                label = { Text(texts.HEALTH_VO2_MAX) },
                suffix = { Text(texts.UNIT_VO2_MAX) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = stepLengthText,
                onValueChange = { if (it.all { c -> c.isDigit() }) stepLengthText = it },
                label = { Text(texts.HEALTH_STEP_LENGTH_CM) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val parseDouble = { s: String, default: Double -> s.replace(',', '.').toDoubleOrNull() ?: default }
                    val parseInt = { s: String, default: Int -> s.toIntOrNull() ?: default }

                    val finalData = data.copy(
                        age = parseInt(ageText, data.age),
                        weight = parseDouble(weightText, data.weight),
                        height = parseDouble(heightText, data.height),
                        restingHR = parseInt(restingHRText, data.restingHR),
                        maxHR = parseInt(maxHRText, data.maxHR),
                        vo2Max = vo2MaxText.replace(',', '.').toDoubleOrNull(),
                        stepLength = parseInt(stepLengthText, data.stepLength)
                    )
                    onSave(finalData)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(texts.SETTINGS_SAVE)
            }
        }
    }
}
