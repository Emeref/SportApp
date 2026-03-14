package com.example.sportapp.presentation.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sportapp.data.db.WorkoutDefinitionDao
import com.example.sportapp.data.model.WorkoutDefinition
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DynamicWorkoutViewModel @Inject constructor(
    private val dao: WorkoutDefinitionDao
) : ViewModel() {

    private val _definition = MutableStateFlow<WorkoutDefinition?>(null)
    val definition: StateFlow<WorkoutDefinition?> = _definition

    fun loadDefinition(id: Long) {
        viewModelScope.launch {
            _definition.value = dao.getDefinitionById(id)
        }
    }
}
