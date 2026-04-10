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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ExerciseImportUiState(
    val sessions: List<ExerciseSessionSyncDto> = emptyList(),
    val isLoading: Boolean = false,
    val isImporting: Boolean = false,
    val currentImport: Int = 0,
    val totalToImport: Int = 0,
    val error: String? = null,
    val showConfirmDialog: Boolean = false,
    val importSuccess: Boolean = false
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
                val sessions = exerciseSyncUseCase.readExerciseSessions(daysBack = 30)
                _uiState.update { it.copy(sessions = sessions, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun toggleSelection(hcSessionId: String) {
        _uiState.update { state ->
            state.copy(
                sessions = state.sessions.map { session ->
                    if (session.hcSessionId == hcSessionId && !session.alreadyImported) {
                        session.copy(isSelected = !session.isSelected)
                    } else {
                        session
                    }
                }
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
                        val timeSeries = exerciseSyncUseCase.readSessionTimeSeries(session.hcSessionId)
                        workoutRepository.saveImportedSession(session, timeSeries)
                    } catch (e: Exception) {
                        Log.e("ExerciseImport", "Failed to load time series for ${session.hcSessionId}", e)
                        // Fallback to summary-only import
                        workoutRepository.saveImportedSession(session, null)
                    }
                }
                
                _uiState.update { it.copy(isImporting = false, currentImport = 0, totalToImport = 0, importSuccess = true) }
                loadSessions() // Reload to show updated "already imported" status
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
