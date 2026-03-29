package com.example.sportapp.presentation.settings

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.ExperimentalWearFoundationApi
import androidx.wear.compose.foundation.rotary.RotaryScrollableDefaults
import androidx.wear.compose.foundation.rotary.rotaryScrollable
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.*
import com.example.sportapp.core.i18n.LocalAppStrings

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
    val strings = LocalAppStrings.current
    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item { ListHeader { Text(strings.healthData) } }
        
        item {
            val genderLabel = when(data.gender) {
                Gender.MALE -> strings.male
                Gender.FEMALE -> strings.female
            }
            Chip(
                label = { Text(strings.gender) },
                secondaryLabel = { Text(genderLabel) },
                onClick = onNavigateToGender,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
            )
        }
        
        item {
            Chip(
                label = { Text(strings.age) },
                secondaryLabel = { Text("${data.age} ${strings.yearsUnit}") },
                onClick = onNavigateToAge,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 2.dp)
            )
        }
        
        item {
            Chip(
                label = { Text(strings.weight) },
                secondaryLabel = { Text("${data.weight} ${strings.kgUnit}") },
                onClick = onNavigateToWeight,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 2.dp)
            )
        }
        
        item {
            Chip(
                label = { Text(strings.height) },
                secondaryLabel = { Text("${data.height} ${strings.cmUnit}") },
                onClick = onNavigateToHeight,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 2.dp)
            )
        }

        item {
            Chip(
                label = { Text(strings.stepLength) },
                secondaryLabel = { Text("${data.stepLength} ${strings.cmUnit}") },
                onClick = onNavigateToStepLength,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 2.dp)
            )
        }

        item {
            Chip(
                label = { Text(strings.restingHeartRate) },
                secondaryLabel = { Text("${data.restingHR} ${strings.bpmUnit}") },
                onClick = onNavigateToRestingHR,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 2.dp)
            )
        }

        item {
            Chip(
                label = { Text(strings.maxHeartRate) },
                secondaryLabel = { Text("${data.maxHR} ${strings.bpmUnit}") },
                onClick = onNavigateToMaxHR,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
            )
        }
    }
}

@OptIn(ExperimentalWearFoundationApi::class)
@Composable
fun NumericInputScreen(
    label: String,
    value: Int,
    range: IntRange,
    unit: String = "",
    onValueChange: (Int) -> Unit,
    onDone: () -> Unit
) {
    val state = rememberPickerState(
        initialNumberOfOptions = range.last - range.first + 1,
        initiallySelectedOption = value - range.first
    )
    val focusRequester = remember { FocusRequester() }
    val strings = LocalAppStrings.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.caption1,
            color = MaterialTheme.colors.secondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 17.dp)
        )
        
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Picker(
                state = state,
                contentDescription = label,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .rotaryScrollable(
                        RotaryScrollableDefaults.behavior(state),
                        focusRequester
                    )
            ) { index ->
                val currentVal = range.first + index
                Text(
                    text = if (unit.isNotEmpty()) "$currentVal $unit" else "$currentVal",
                    style = MaterialTheme.typography.display2,
                    color = if (state.selectedOption == index) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface
                )
            }
        }

        Chip(
            label = { Text(strings.save, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) },
            onClick = {
                onValueChange(range.first + state.selectedOption)
                onDone()
            },
            modifier = Modifier
                .width(120.dp)
                .padding(bottom = 12.dp),
            colors = ChipDefaults.primaryChipColors()
        )
    }
}

@Composable
fun GenderSelectionScreen(selected: Gender, onSelected: (Gender) -> Unit) {
    val listState = rememberScalingLazyListState()
    val strings = LocalAppStrings.current
    ScalingLazyColumn(modifier = Modifier.fillMaxSize(), state = listState) {
        item { ListHeader { Text(strings.selectGender) } }
        Gender.values().forEach { gender ->
            val genderLabel = when(gender) {
                Gender.MALE -> strings.male
                Gender.FEMALE -> strings.female
            }
            item {
                ToggleChip(
                    checked = selected == gender,
                    onCheckedChange = { if (it) onSelected(gender) },
                    label = { Text(genderLabel) },
                    toggleControl = { RadioButton(selected == gender) },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }
    }
}
