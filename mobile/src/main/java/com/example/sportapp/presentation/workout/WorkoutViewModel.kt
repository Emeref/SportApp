package com.example.sportapp.presentation.workout

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sportapp.data.WorkoutTrackingService
import com.example.sportapp.data.db.WorkoutDefinitionDao
import com.example.sportapp.data.model.WorkoutDefinition
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkoutViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val definitionDao: WorkoutDefinitionDao
) : ViewModel() {

    val definitions: StateFlow<List<WorkoutDefinition>> = definitionDao.getAllDefinitions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedDefinition = MutableStateFlow<WorkoutDefinition?>(null)
    val selectedDefinition = _selectedDefinition.asStateFlow()

    private val _isTracking = MutableStateFlow(false)
    val isTracking = _isTracking.asStateFlow()

    fun selectDefinition(definition: WorkoutDefinition) {
        _selectedDefinition.value = definition
    }

    fun loadDefinition(id: Long) {
        viewModelScope.launch {
            val def = definitionDao.getDefinitionById(id)
            _selectedDefinition.value = def
        }
    }

    fun startWorkout() {
        val definition = _selectedDefinition.value ?: return
        val intent = Intent(context, WorkoutTrackingService::class.java).apply {
            action = WorkoutTrackingService.ACTION_START
            putExtra(WorkoutTrackingService.EXTRA_ACTIVITY_NAME, definition.name)
        }
        context.startForegroundService(intent)
        _isTracking.value = true
    }

    fun pauseWorkout() {
        val intent = Intent(context, WorkoutTrackingService::class.java).apply {
            action = WorkoutTrackingService.ACTION_PAUSE
        }
        context.startService(intent)
    }

    fun resumeWorkout() {
        val intent = Intent(context, WorkoutTrackingService::class.java).apply {
            action = WorkoutTrackingService.ACTION_RESUME
        }
        context.startService(intent)
    }

    fun stopWorkout() {
        val intent = Intent(context, WorkoutTrackingService::class.java).apply {
            action = WorkoutTrackingService.ACTION_STOP
        }
        context.startService(intent)
        _isTracking.value = false
    }
}
