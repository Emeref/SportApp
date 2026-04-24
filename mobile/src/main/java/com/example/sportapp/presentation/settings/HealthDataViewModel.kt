package com.example.sportapp.presentation.settings

import android.util.Log
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sportapp.data.IUserHealthRepository
import com.example.sportapp.healthconnect.HealthDataSyncResult
import com.example.sportapp.healthconnect.HealthDataSyncUseCase
import com.example.sportapp.healthconnect.HealthConnectManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class HealthField {
    WEIGHT, HEIGHT, RESTING_HR, MAX_HR, VO2_MAX
}

@HiltViewModel
class HealthDataViewModel @Inject constructor(
    private val healthConnectManager: HealthConnectManager,
    private val syncUseCase: HealthDataSyncUseCase,
    private val repository: IUserHealthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HealthDataSyncState())
    val uiState = _uiState.asStateFlow()

    val profilePermissions: Set<String>
        get() = healthConnectManager.profilePermissions

    init {
        checkHealthConnectAvailability()
    }

    fun checkHealthConnectAvailability() {
        val available = healthConnectManager.isAvailable()
        Log.d("HealthDataVM", "Health Connect available: $available")
        _uiState.update { it.copy(isAvailable = available) }
    }

    fun onSyncClick(onPermissionRequired: (Set<String>) -> Unit) {
        viewModelScope.launch {
            val permissions = healthConnectManager.profilePermissions
            if (healthConnectManager.hasPermissions(permissions)) {
                prepareFieldSelection()
            } else {
                onPermissionRequired(permissions)
            }
        }
    }

    private suspend fun prepareFieldSelection() {
        val availableFields = mutableListOf<HealthField>()
        
        if (healthConnectManager.hasPermissions(setOf(HealthPermission.getReadPermission(WeightRecord::class)))) {
            availableFields.add(HealthField.WEIGHT)
        }
        if (healthConnectManager.hasPermissions(setOf(HealthPermission.getReadPermission(HeightRecord::class)))) {
            availableFields.add(HealthField.HEIGHT)
        }
        if (healthConnectManager.hasPermissions(setOf(HealthPermission.getReadPermission(RestingHeartRateRecord::class)))) {
            availableFields.add(HealthField.RESTING_HR)
        }
        if (healthConnectManager.hasPermissions(setOf(HealthPermission.getReadPermission(HeartRateRecord::class)))) {
            availableFields.add(HealthField.MAX_HR)
        }
        if (healthConnectManager.hasPermissions(setOf(HealthPermission.getReadPermission(Vo2MaxRecord::class)))) {
            availableFields.add(HealthField.VO2_MAX)
        }

        _uiState.update { 
            it.copy(
                showFieldSelectionDialog = true,
                availableFields = availableFields,
                selectedFields = availableFields.toSet()
            ) 
        }
    }

    fun toggleField(field: HealthField) {
        _uiState.update { state ->
            val newSelected = if (state.selectedFields.contains(field)) {
                state.selectedFields - field
            } else {
                state.selectedFields + field
            }
            state.copy(selectedFields = newSelected)
        }
    }

    fun onCancelFieldSelection() {
        _uiState.update { it.copy(showFieldSelectionDialog = false) }
    }

    fun onConfirmFieldSelection() {
        val selected = _uiState.value.selectedFields
        if (selected.isEmpty()) {
            _uiState.update { it.copy(showFieldSelectionDialog = false) }
            return
        }
        _uiState.update { it.copy(showFieldSelectionDialog = false) }
        syncHealthData(selected)
    }

    fun syncHealthData(fieldsToSync: Set<HealthField>? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val result = syncUseCase.readHealthDataFromHC()
                when (result) {
                    is HealthDataSyncResult.Success -> {
                        val preview = PreviewHealthData(
                            weight = if (fieldsToSync?.contains(HealthField.WEIGHT) != false) result.weightKg else null,
                            height = if (fieldsToSync?.contains(HealthField.HEIGHT) != false) result.heightCm else null,
                            restingHR = if (fieldsToSync?.contains(HealthField.RESTING_HR) != false) result.restingHeartRate else null,
                            maxHR = if (fieldsToSync?.contains(HealthField.MAX_HR) != false) result.maxHeartRate else null,
                            vo2Max = if (fieldsToSync?.contains(HealthField.VO2_MAX) != false) result.vo2max else null,
                            age = result.age,
                            sex = result.sex
                        )

                        if (preview.weight != null || preview.height != null || preview.restingHR != null || 
                            preview.maxHR != null || preview.vo2Max != null) {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    showPreviewDialog = true,
                                    previewData = preview
                                )
                            }
                        } else {
                            _uiState.update { it.copy(isLoading = false, error = "Brak nowych danych dla wybranych pól") }
                        }
                    }
                    is HealthDataSyncResult.PermissionDenied -> {
                        _uiState.update { it.copy(isLoading = false, error = "Brak uprawnień do odczytu danych") }
                    }
                    is HealthDataSyncResult.Error -> {
                        _uiState.update { it.copy(isLoading = false, error = result.message) }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Błąd synchronizacji") }
            }
        }
    }

    fun onConfirmSync(currentData: HealthData, onDataUpdated: (HealthData) -> Unit) {
        val preview = _uiState.value.previewData ?: return
        val updatedData = currentData.copy(
            weight = preview.weight ?: currentData.weight,
            height = preview.height ?: currentData.height,
            restingHR = preview.restingHR ?: currentData.restingHR,
            maxHR = preview.maxHR ?: currentData.maxHR,
            vo2Max = preview.vo2Max ?: currentData.vo2Max,
            age = preview.age ?: currentData.age,
            gender = when (preview.sex) {
                "male" -> Gender.MALE
                "female" -> Gender.FEMALE
                else -> currentData.gender
            }
        )
        
        viewModelScope.launch {
            repository.updateHealthData(updatedData)
            _uiState.update { it.copy(showPreviewDialog = false, syncConfirmed = true) }
            onDataUpdated(updatedData)
        }
    }

    fun onDismissPreview() {
        _uiState.update { it.copy(showPreviewDialog = false) }
    }
    
    fun resetSyncConfirmed() {
        _uiState.update { it.copy(syncConfirmed = false) }
    }

    fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }
}

data class HealthDataSyncState(
    val isAvailable: Boolean = false,
    val isLoading: Boolean = false,
    val showPreviewDialog: Boolean = false,
    val showFieldSelectionDialog: Boolean = false,
    val availableFields: List<HealthField> = emptyList(),
    val selectedFields: Set<HealthField> = emptySet(),
    val previewData: PreviewHealthData? = null,
    val syncConfirmed: Boolean = false,
    val error: String? = null
)

data class PreviewHealthData(
    val weight: Double? = null,
    val height: Double? = null,
    val restingHR: Int? = null,
    val maxHR: Int? = null,
    val vo2Max: Double? = null,
    val age: Int? = null,
    val sex: String? = null
)
