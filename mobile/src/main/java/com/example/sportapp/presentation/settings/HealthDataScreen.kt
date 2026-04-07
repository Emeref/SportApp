package com.example.sportapp.presentation.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.sportapp.LocalMobileTexts

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthDataScreen(
    initialData: HealthData,
    onSave: (HealthData) -> Unit,
    onCancel: () -> Unit
) {
    val texts = LocalMobileTexts.current
    var data by remember { mutableStateOf(initialData) }
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(texts.HEALTH_TITLE) },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = texts.SETTINGS_CLOSE)
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
                onValueChange = { data = data.copy(weight = it.toIntOrNull() ?: data.weight) },
                label = { Text(texts.HEALTH_WEIGHT_KG) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = data.height.toString(),
                onValueChange = { data = data.copy(height = it.toIntOrNull() ?: data.height) },
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
