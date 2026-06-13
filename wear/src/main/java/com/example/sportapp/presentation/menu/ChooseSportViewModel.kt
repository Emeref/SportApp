package com.example.sportapp.presentation.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sportapp.data.db.WorkoutDefinitionDao
import com.example.sportapp.data.model.WorkoutDefinition
import com.example.sportapp.data.model.createDefaultWorkoutDefinition
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
            // Requirement: Twórz standardową aktywność tylko gdy nie ma żadnej innej dostępnej
            if (dao.getCount() == 0) {
                val defaultDef = createDefaultWorkoutDefinition()
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
