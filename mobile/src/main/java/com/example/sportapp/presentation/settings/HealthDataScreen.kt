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
import com.example.sportapp.core.i18n.LocalAppStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthDataScreen(
    initialData: HealthData,
    onSave: (HealthData) -> Unit,
    onCancel: () -> Unit
) {
    var data by remember { mutableStateOf(initialData) }
    val scrollState = rememberScrollState()
    val strings = LocalAppStrings.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(strings.healthData) },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = strings.back)
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
            Text(strings.gender, style = MaterialTheme.typography.titleMedium)
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = data.gender == Gender.MALE,
                    onClick = { data = data.copy(gender = Gender.MALE) }
                )
                Text(strings.maleLabel, modifier = Modifier.padding(end = 16.dp))
                RadioButton(
                    selected = data.gender == Gender.FEMALE,
                    onClick = { data = data.copy(gender = Gender.FEMALE) }
                )
                Text(strings.femaleLabel)
            }

            OutlinedTextField(
                value = data.age.toString(),
                onValueChange = { data = data.copy(age = it.toIntOrNull() ?: data.age) },
                label = { Text(strings.age) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = data.weight.toString(),
                onValueChange = { data = data.copy(weight = it.toIntOrNull() ?: data.weight) },
                label = { Text("${strings.weight} (${strings.kgUnit})") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = data.height.toString(),
                onValueChange = { data = data.copy(height = it.toIntOrNull() ?: data.height) },
                label = { Text("${strings.height} (${strings.cmUnit})") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = data.restingHR.toString(),
                onValueChange = { data = data.copy(restingHR = it.toIntOrNull() ?: data.restingHR) },
                label = { Text(strings.restingHeartRate) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = data.maxHR.toString(),
                onValueChange = { data = data.copy(maxHR = it.toIntOrNull() ?: data.maxHR) },
                label = { Text("${strings.maxHeartRate} (HR Max)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = data.stepLength.toString(),
                onValueChange = { data = data.copy(stepLength = it.toIntOrNull() ?: data.stepLength) },
                label = { Text("${strings.stepLength} (${strings.cmUnit})") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { onSave(data) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(strings.save)
            }
        }
    }
}
