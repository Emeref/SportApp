package com.example.sportapp.presentation.definitions

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sportapp.core.i18n.PlStrings
import com.example.sportapp.data.db.WorkoutDefinitionDao
import com.example.sportapp.data.model.SensorConfig
import com.example.sportapp.data.model.WorkoutDefinition
import com.example.sportapp.data.model.WorkoutSensor
import com.example.sportapp.data.WorkoutDefinitionSyncManager
import com.example.sportapp.presentation.stats.ActivityDetailSettingsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkoutDefinitionViewModel @Inject constructor(
    private val dao: WorkoutDefinitionDao,
    private val syncManager: WorkoutDefinitionSyncManager,
    @ApplicationContext context: Context
) : ViewModel() {

    private val detailSettingsManager = ActivityDetailSettingsManager(context)

    val definitions: StateFlow<List<WorkoutDefinition>> = dao.getAllDefinitions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        ensureDefaultDefinition()
        observeAndSync()
    }

    private fun ensureDefaultDefinition() {
        viewModelScope.launch {
            if (dao.getDefaultCount() == 0) {
                val defaultSensors = WorkoutSensor.entries.map {
                    SensorConfig(it.id, isVisible = true, isRecording = true)
                }
                // Using PlStrings as reference for initial system creation. 
                // In a perfect world we might want to check system locale here or use LocalAppStrings, 
                // but ViewModel init doesn't have easy access to CompositionLocal.
                // We use standardActivity from PlStrings as the default name.
                val defaultDef = WorkoutDefinition(
                    name = PlStrings.standardActivity,
                    iconName = "DirectionsRun",
                    sensors = defaultSensors,
                    baseType = "Other",
                    isDefault = true,
                    sortOrder = 0
                )
                dao.insertDefinition(defaultDef)
            }
        }
    }

    private fun observeAndSync() {
        viewModelScope.launch {
            definitions.collectLatest { list ->
                if (list.isNotEmpty()) {
                    syncManager.syncDefinitions(list)
                }
            }
        }
    }

    fun saveDefinition(definition: WorkoutDefinition) {
        viewModelScope.launch {
            if (definition.id == 0L) {
                val currentMaxOrder = definitions.value.maxOfOrNull { it.sortOrder } ?: -1
                dao.insertDefinition(definition.copy(sortOrder = currentMaxOrder + 1))
            } else {
                dao.updateDefinition(definition)
            }
        }
    }

    fun deleteDefinition(definition: WorkoutDefinition) {
        if (definition.isDefault) return
        viewModelScope.launch {
            dao.deleteDefinition(definition)
            detailSettingsManager.deleteSettings(definition.name)
            reorderAfterDeletion()
        }
    }

    private suspend fun reorderAfterDeletion() {
        val currentList = dao.getAllDefinitionsOnce()
        val updatedList = currentList.mapIndexed { index, def ->
            def.copy(sortOrder = index)
        }
        dao.updateDefinitions(updatedList)
    }

    fun moveUp(definition: WorkoutDefinition) {
        val list = definitions.value.toMutableList()
        val index = list.indexOfFirst { it.id == definition.id }
        if (index > 0) {
            val other = list[index - 1]
            list[index - 1] = definition.copy(sortOrder = index - 1)
            list[index] = other.copy(sortOrder = index)
            viewModelScope.launch {
                dao.updateDefinitions(list)
            }
        }
    }

    fun moveDown(definition: WorkoutDefinition) {
        val list = definitions.value.toMutableList()
        val index = list.indexOfFirst { it.id == definition.id }
        if (index != -1 && index < list.size - 1) {
            val other = list[index + 1]
            list[index + 1] = definition.copy(sortOrder = index + 1)
            list[index] = other.copy(sortOrder = index)
            viewModelScope.launch {
                dao.updateDefinitions(list)
            }
        }
    }
}
