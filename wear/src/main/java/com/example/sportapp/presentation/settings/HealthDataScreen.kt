package com.example.sportapp.presentation.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.*

@Composable
fun HealthDataScreen(
    data: HealthData,
    onNavigateToGender: () -> Unit,
    onNavigateToAge: () -> Unit,
    onNavigateToWeight: () -> Unit,
    onNavigateToHeight: () -> Unit,
    onNavigateToRestingHR: () -> Unit,
    onNavigateToMaxHR: () -> Unit,
    onNavigateToStepLength: () -> Unit
) {
    val listState = rememberScalingLazyListState()
    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item { ListHeader { Text("Dane zdrowotne") } }
        
        item {
            Chip(
                label = { Text("Płeć") },
                secondaryLabel = { Text(data.gender.toPolish()) },
                onClick = onNavigateToGender,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
            )
        }
        
        item {
            Chip(
                label = { Text("Wiek") },
                secondaryLabel = { Text("${data.age} lat") },
                onClick = onNavigateToAge,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 2.dp)
            )
        }
        
        item {
            Chip(
                label = { Text("Waga") },
                secondaryLabel = { Text("${data.weight} kg") },
                onClick = onNavigateToWeight,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 2.dp)
            )
        }
        
        item {
            Chip(
                label = { Text("Wzrost") },
                secondaryLabel = { Text("${data.height} cm") },
                onClick = onNavigateToHeight,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 2.dp)
            )
        }

        item {
            Chip(
                label = { Text("Długość kroku") },
                secondaryLabel = { Text("${data.stepLength} cm") },
                onClick = onNavigateToStepLength,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 2.dp)
            )
        }

        item {
            Chip(
                label = { Text("Tętno spoczynkowe") },
                secondaryLabel = { Text("${data.restingHR} BPM") },
                onClick = onNavigateToRestingHR,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 2.dp)
            )
        }

        item {
            Chip(
                label = { Text("Tętno maksymalne") },
                secondaryLabel = { Text("${data.maxHR} BPM") },
                onClick = onNavigateToMaxHR,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
            )
        }
    }
}

@Composable
fun NumericInputScreen(
    value: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit,
    onDone: () -> Unit
) {
    var textFieldValue by remember {
        mutableStateOf(
            TextFieldValue(
                text = value.toString(),
                selection = TextRange(value.toString().length)
            )
        )
    }
    val focusRequester = remember { FocusRequester() }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        BasicTextField(
            value = textFieldValue,
            onValueChange = { newValue ->
                val text = newValue.text
                if (text.isEmpty() || (text.all { it.isDigit() } && text.length <= 3)) {
                    textFieldValue = newValue
                    text.toIntOrNull()?.let { num ->
                        onValueChange(num.coerceIn(range.first, range.last))
                    }
                }
            },
            modifier = Modifier
                .size(1.dp)
                .focusRequester(focusRequester),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { onDone() }
            ),
            textStyle = androidx.compose.ui.text.TextStyle(color = Color.Transparent)
        )
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Composable
fun GenderSelectionScreen(selected: Gender, onSelected: (Gender) -> Unit) {
    val listState = rememberScalingLazyListState()
    ScalingLazyColumn(modifier = Modifier.fillMaxSize(), state = listState) {
        item { ListHeader { Text("Wybierz płeć") } }
        Gender.values().forEach { gender ->
            item {
                ToggleChip(
                    checked = selected == gender,
                    onCheckedChange = { if (it) onSelected(gender) },
                    label = { Text(gender.toPolish()) },
                    toggleControl = { RadioButton(selected == gender) },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }
    }
}
