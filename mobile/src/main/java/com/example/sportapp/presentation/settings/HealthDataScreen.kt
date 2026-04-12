package com.example.sportapp.presentation.settings

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
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
    val scrollState = rememberScrollState()
    val uiState by viewModel.uiState.collectAsState()

    val permissionsLauncher = rememberLauncherForActivityResult(
        contract = androidx.health.connect.client.PermissionController.createRequestPermissionResultContract()
    ) { granted ->
        if (granted.isNotEmpty()) {
            viewModel.syncHealthData()
        }
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
                value = data.age.toString(),
                onValueChange = { data = data.copy(age = it.toIntOrNull() ?: data.age) },
                label = { Text(texts.HEALTH_AGE) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = data.weight.toString(),
                onValueChange = { data = data.copy(weight = it.toDoubleOrNull() ?: data.weight) },
                label = { Text(texts.HEALTH_WEIGHT_KG) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = data.height.toString(),
                onValueChange = { data = data.copy(height = it.toDoubleOrNull() ?: data.height) },
                label = { Text(texts.HEALTH_HEIGHT_CM) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = data.restingHR.toString(),
                onValueChange = { data = data.copy(restingHR = it.toIntOrNull() ?: data.restingHR) },
                label = { Text(texts.HEALTH_RESTING_HR) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = data.maxHR.toString(),
                onValueChange = { data = data.copy(maxHR = it.toIntOrNull() ?: data.maxHR) },
                label = { Text(texts.HEALTH_MAX_HR) },
                supportingText = { Text(texts.HEALTH_MAX_HR_DESC) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = data.vo2Max?.let { String.format(Locale.US, "%.2f", it) } ?: "",
                onValueChange = { 
                    val newValue = it.toDoubleOrNull()
                    if (newValue != null || it.isEmpty()) {
                        data = data.copy(vo2Max = newValue)
                    }
                },
                label = { Text(texts.HEALTH_VO2_MAX) },
                suffix = { Text(texts.UNIT_VO2_MAX) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = data.stepLength.toString(),
                onValueChange = { data = data.copy(stepLength = it.toIntOrNull() ?: data.stepLength) },
                label = { Text(texts.HEALTH_STEP_LENGTH_CM) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { onSave(data) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(texts.SETTINGS_SAVE)
            }
        }
    }
}
