package com.example.sportapp.presentation.definitions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sportapp.data.db.WorkoutDefinitionDao
import com.example.sportapp.data.model.SensorConfig
import com.example.sportapp.data.model.WorkoutDefinition
import com.example.sportapp.data.model.WorkoutSensor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkoutDefinitionViewModel @Inject constructor(
    private val dao: WorkoutDefinitionDao
) : ViewModel() {

    val definitions: StateFlow<List<WorkoutDefinition>> = dao.getAllDefinitions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        ensureDefaultDefinition()
    }

    private fun ensureDefaultDefinition() {
        viewModelScope.launch {
            if (dao.getDefaultCount() == 0) {
                val defaultSensors = WorkoutSensor.entries.map {
                    SensorConfig(it.id, isVisible = true, isRecording = true)
                }
                val defaultDef = WorkoutDefinition(
                    name = "Standardowa aktywność",
                    iconName = "DirectionsRun",
                    sensors = defaultSensors,
                    baseType = "Other",
                    isDefault = true
                )
                dao.insertDefinition(defaultDef)
            }
        }
    }

    fun saveDefinition(definition: WorkoutDefinition) {
        viewModelScope.launch {
            if (definition.id == 0L) {
                dao.insertDefinition(definition)
            } else {
                dao.updateDefinition(definition)
            }
            // TODO: Trigger synchronization with watch
        }
    }

    fun deleteDefinition(definition: WorkoutDefinition) {
        if (definition.isDefault) return
        viewModelScope.launch {
            dao.deleteDefinition(definition)
            // TODO: Trigger synchronization with watch
        }
    }
}
