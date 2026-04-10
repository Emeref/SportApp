package com.example.sportapp.presentation.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sportapp.data.IWorkoutRepository
import com.example.sportapp.healthconnect.ExerciseSyncUseCase
import com.example.sportapp.healthconnect.model.ExerciseSessionSyncDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.health.connect.client.records.ExerciseSessionRecord

data class ExerciseImportUiState(
    val sessions: List<ExerciseSessionSyncDto> = emptyList(),
    val isLoading: Boolean = false,
    val isImporting: Boolean = false,
    val currentImport: Int = 0,
    val totalToImport: Int = 0,
    val error: String? = null,
    val showConfirmDialog: Boolean = false,
    val importSuccess: Boolean = false,
    val allSelected: Boolean = false
)

@HiltViewModel
class ExerciseImportViewModel @Inject constructor(
    private val exerciseSyncUseCase: ExerciseSyncUseCase,
    private val workoutRepository: IWorkoutRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExerciseImportUiState())
    val uiState: StateFlow<ExerciseImportUiState> = _uiState.asStateFlow()

    init {
        loadSessions()
    }

    fun loadSessions() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val rawSessions = exerciseSyncUseCase.readExerciseSessions(daysBack = 30)
                val definitions = workoutRepository.getAllDefinitions().first()
                
                val sessions = rawSessions.map { session ->
                    val baseType = mapHcToAppBaseType(session.exerciseType)
                    val mappedTitle = if (session.title == "Workout" || session.title.isBlank()) {
                        definitions.find { it.baseType == baseType }?.name ?: session.title
                    } else {
                        session.title
                    }
                    session.copy(title = mappedTitle)
                }

                _uiState.update { state -> 
                    val updatedSessions = sessions.map { it.copy(isSelected = !it.alreadyImported) }
                    state.copy(
                        sessions = updatedSessions, 
                        isLoading = false,
                        allSelected = updatedSessions.any { it.isSelected }
                    ) 
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun mapHcToAppBaseType(hcType: Int): String {
        return when (hcType) {
            ExerciseSessionRecord.EXERCISE_TYPE_RUNNING -> "Bieganie"
            ExerciseSessionRecord.EXERCISE_TYPE_WALKING -> "Chodzenie"
            ExerciseSessionRecord.EXERCISE_TYPE_BIKING -> "Jazda na rowerze"
            ExerciseSessionRecord.EXERCISE_TYPE_HIKING -> "Wędrówka"
            else -> "Inne"
        }
    }

    fun toggleSelection(hcSessionId: String) {
        _uiState.update { state ->
            val newSessions = state.sessions.map { session ->
                if (session.hcSessionId == hcSessionId && !session.alreadyImported) {
                    session.copy(isSelected = !session.isSelected)
                } else {
                    session
                }
            }
            state.copy(
                sessions = newSessions,
                allSelected = newSessions.filter { !it.alreadyImported }.all { it.isSelected }
            )
        }
    }

    fun toggleSelectAll(selected: Boolean) {
        _uiState.update { state ->
            val newSessions = state.sessions.map { 
                if (!it.alreadyImported) it.copy(isSelected = selected) else it 
            }
            state.copy(
                sessions = newSessions,
                allSelected = selected
            )
        }
    }

    fun onImportClick() {
        val selectedCount = _uiState.value.sessions.count { it.isSelected && !it.alreadyImported }
        if (selectedCount > 0) {
            _uiState.update { it.copy(showConfirmDialog = true) }
        }
    }

    fun onDismissConfirm() {
        _uiState.update { it.copy(showConfirmDialog = false) }
    }

    fun confirmImport() {
        viewModelScope.launch {
            _uiState.update { it.copy(showConfirmDialog = false, isImporting = true) }
            try {
                val toImport = _uiState.value.sessions.filter { it.isSelected && !it.alreadyImported }
                val total = toImport.size
                
                toImport.forEachIndexed { index, session ->
                    val current = index + 1
                    _uiState.update { it.copy(currentImport = current, totalToImport = total) }
                    
                    try {
                        // Dodatkowe sprawdzenie duplikatu tuż przed zapisem (np. po czasie startu)
                        val isDuplicate = workoutRepository.getAllWorkouts().first().any { 
                            it.startTime == session.startTime.toEpochMilli() && 
                            it.durationSeconds == java.time.Duration.between(session.startTime, session.endTime).seconds
                        }
                        
                        if (!isDuplicate) {
                            val timeSeries = exerciseSyncUseCase.readSessionTimeSeries(session.hcSessionId)
                            workoutRepository.saveImportedSession(session, timeSeries)
                        } else {
                            Log.d("ExerciseImport", "Skipping duplicate workout at ${session.startTime}")
                        }
                    } catch (e: Exception) {
                        Log.e("ExerciseImport", "Failed to load time series for ${session.hcSessionId}", e)
                        workoutRepository.saveImportedSession(session, null)
                    }
                }
                
                _uiState.update { it.copy(isImporting = false, currentImport = 0, totalToImport = 0, importSuccess = true) }
                loadSessions()
            } catch (e: Exception) {
                _uiState.update { it.copy(isImporting = false, currentImport = 0, totalToImport = 0, error = "Import failed: ${e.message}") }
            }
        }
    }

    fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }
    
    fun dismissSuccess() {
        _uiState.update { it.copy(importSuccess = false) }
    }
}
