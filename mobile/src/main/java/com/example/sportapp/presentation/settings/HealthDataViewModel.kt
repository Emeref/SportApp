package com.example.sportapp.presentation.settings

import android.util.Log
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
            Log.d("HealthDataVM", "Checking permissions: $permissions")
            if (healthConnectManager.hasPermissions(permissions)) {
                Log.d("HealthDataVM", "Permissions granted, starting sync")
                syncHealthData()
            } else {
                Log.d("HealthDataVM", "Permissions required")
                onPermissionRequired(permissions)
            }
        }
    }

    fun syncHealthData() {
        viewModelScope.launch {
            Log.d("HealthDataVM", "syncHealthData started")
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val result = syncUseCase.readHealthDataFromHC()
                Log.d("HealthDataVM", "Sync result: $result")
                when (result) {
                    is HealthDataSyncResult.Success -> {
                        if (result.weightKg != null || result.heightCm != null || result.restingHeartRate != null || 
                            result.maxHeartRate != null || result.vo2max != null) {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    showPreviewDialog = true,
                                    previewData = PreviewHealthData(
                                        weight = result.weightKg,
                                        height = result.heightCm,
                                        restingHR = result.restingHeartRate,
                                        maxHR = result.maxHeartRate,
                                        vo2Max = result.vo2max,
                                        age = result.age,
                                        sex = result.sex
                                    )
                                )
                            }
                        } else {
                            _uiState.update { it.copy(isLoading = false, error = "Brak nowych danych w Health Connect") }
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
                Log.e("HealthDataVM", "Sync error", e)
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
