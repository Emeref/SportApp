package com.example.sportapp.presentation.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sportapp.data.db.WorkoutDefinitionDao
import com.example.sportapp.data.model.SensorConfig
import com.example.sportapp.data.model.WorkoutDefinition
import com.example.sportapp.data.model.WorkoutSensor
import com.example.sportapp.presentation.workout.DataLayerManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChooseSportViewModel @Inject constructor(
    private val dao: WorkoutDefinitionDao,
    private val dataLayerManager: DataLayerManager
) : ViewModel() {

    // Zmieniamy na nullable, aby odróżnić ładowanie od pustej listy
    val definitions: StateFlow<List<WorkoutDefinition>?> = dao.getAllDefinitions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    init {
        ensureDefaultDefinition()
        refreshFromPhone()
    }

    private fun ensureDefaultDefinition() {
        viewModelScope.launch {
            if (dao.getDefaultCount() == 0) {
                val defaultSensors = WorkoutSensor.values().map {
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

    private fun refreshFromPhone() {
        viewModelScope.launch {
            dataLayerManager.requestDefinitionsSync()
        }
    }
}
