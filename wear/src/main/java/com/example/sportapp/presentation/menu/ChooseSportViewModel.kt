package com.example.sportapp.presentation.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sportapp.data.db.WorkoutDefinitionDao
import com.example.sportapp.data.model.WorkoutDefinition
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ChooseSportViewModel @Inject constructor(
    private val dao: WorkoutDefinitionDao
) : ViewModel() {

    val definitions: StateFlow<List<WorkoutDefinition>> = dao.getAllDefinitions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
