package com.example.sportapp.presentation.stats

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sportapp.data.IWorkoutRepository
import com.example.sportapp.data.LapManager
import com.example.sportapp.data.SessionData
import com.example.sportapp.data.SessionRepository
import com.example.sportapp.data.db.WorkoutDao
import com.example.sportapp.data.db.WorkoutPointEntity
import com.example.sportapp.data.model.WorkoutLap
import com.example.sportapp.data.model.HeartRateZoneResult
import com.example.sportapp.presentation.settings.MobileSettingsManager
import com.example.sportapp.presentation.settings.MobileSettingsState
import com.example.sportapp.presentation.settings.WidgetItem
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ActivityDetailViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val repository: IWorkoutRepository,
    private val sessionRepository: SessionRepository,
    private val workoutDao: WorkoutDao,
    private val lapManager: LapManager,
    private val mobileSettingsManager: MobileSettingsManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val activityId: Long = savedStateHandle.get<String>("activityId")?.toLongOrNull() ?: -1L
    private val settingsManager = ActivityDetailSettingsManager(context)

    private val _sessionData = MutableStateFlow<SessionData?>(null)
    val sessionData = _sessionData.asStateFlow()

    private val _laps = MutableStateFlow<List<WorkoutLap>>(emptyList())
    val laps = _laps.asStateFlow()

    private val _selectedLap = MutableStateFlow<WorkoutLap?>(null)
    val selectedLap = _selectedLap.asStateFlow()

    private val _hrZoneResult = MutableStateFlow<HeartRateZoneResult?>(null)
    val hrZoneResult = _hrZoneResult.asStateFlow()

    private val _autoLapDistance = MutableStateFlow<Double?>(null)
    val autoLapDistance = _autoLapDistance.asStateFlow()

    val settings: StateFlow<ActivityDetailSettings> = _sessionData
        .filterNotNull()
        .flatMapLatest { data -> 
            settingsManager.getSettingsFlow(data.activityName)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ActivityDetailSettings(
                visibleCharts = ActivityDetailSettingsManager.DEFAULT_CHARTS,
                visibleWidgets = ActivityDetailSettingsManager.DEFAULT_WIDGETS,
                trackColor = ActivityDetailSettingsManager.DEFAULT_COLOR
            )
        )

    val mobileSettings: StateFlow<MobileSettingsState> = mobileSettingsManager.settingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MobileSettingsState()
        )

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    val chartProducers: Map<String, ChartEntryModelProducer> = mapOf(
        "bpm" to ChartEntryModelProducer(),
        "kalorie_min" to ChartEntryModelProducer(),
        "kalorie_suma" to ChartEntryModelProducer(),
        "kroki_min" to ChartEntryModelProducer(),
        "odl_kroki" to ChartEntryModelProducer(),
        "predkosc_kroki" to ChartEntryModelProducer(),
        "gps_dystans" to ChartEntryModelProducer(),
        "predkosc" to ChartEntryModelProducer(),
        "wysokosc" to ChartEntryModelProducer(),
        "przewyzszenia_gora" to ChartEntryModelProducer(),
        "przewyzszenia_dol" to ChartEntryModelProducer(),
        "pressure" to ChartEntryModelProducer()
    )

    init {
        loadSessionData()
        loadLaps()
    }

    private fun loadSessionData() {
        viewModelScope.launch {
            if (activityId == -1L) {
                _error.value = "Nieprawidłowe ID aktywności."
                return@launch
            }

            try {
                val data = sessionRepository.getSessionData(activityId)
                if (data.error != null) {
                    _error.value = data.error
                } else {
                    _sessionData.value = data
                    updateCharts(data)
                    checkAndGenerateLaps(data)
                    calculateHeartRateZones(activityId)
                    loadAutoLapDistance(data.activityName)
                }
            } catch (e: Exception) {
                _error.value = "Błąd ładowania danych: ${e.message}"
            }
        }
    }

    private fun calculateHeartRateZones(workoutId: Long) {
        viewModelScope.launch {
            val points = workoutDao.getPointsForWorkout(workoutId)
            val settings = mobileSettingsManager.settingsFlow.first()
            val maxHr = settings.healthData.maxHR
            
            _hrZoneResult.value = HeartRateMath.calculateZones(points, maxHr)
        }
    }

    private fun loadLaps() {
        viewModelScope.launch {
            if (activityId != -1L) {
                _laps.value = workoutDao.getLapsForWorkout(activityId)
            }
        }
    }

    private suspend fun loadAutoLapDistance(activityName: String) {
        val definitions = repository.getAllDefinitions().first()
        val definition = definitions.find { it.name == activityName }
        _autoLapDistance.value = definition?.autoLapDistance
    }

    private suspend fun checkAndGenerateLaps(data: SessionData) {
        if (activityId == -1L) return
        
        val existingLaps = workoutDao.getLapsForWorkout(activityId)
        if (existingLaps.isNotEmpty()) return

        val workout = workoutDao.getWorkoutById(activityId) ?: return
        val definitions = repository.getAllDefinitions().first()
        val definition = definitions.find { it.name == workout.activityName }
        
        val autoLapDist = definition?.autoLapDistance
        if (autoLapDist != null && autoLapDist > 0.0) {
            val points = workoutDao.getPointsForWorkout(activityId)
            val generatedLaps = lapManager.processLaps(activityId, points, autoLapDist)
            if (generatedLaps.isNotEmpty()) {
                workoutDao.insertLaps(generatedLaps)
                _laps.value = workoutDao.getLapsForWorkout(activityId)
            }
        }
    }

    private fun updateCharts(data: SessionData) {
        chartProducers.forEach { (id, producer) ->
            val points = data.charts[id] ?: emptyList()
            if (points.isNotEmpty()) {
                val entries = if (id == "bpm" || id == "kroki_min" || id == "wysokosc" || id == "predkosc" || id == "predkosc_kroki" || id == "pressure") {
                    // Wygładzanie średnią kroczącą z 10 sekund dla wybranych metryk
                    points.windowed(10, 1, true) { window ->
                        val valid = window.filterNotNull()
                        if (valid.isEmpty()) null else valid.average().toFloat()
                    }.mapIndexedNotNull { index, value ->
                        if (value == null || value.isNaN()) null else entryOf(index, value)
                    }
                } else {
                    points.mapIndexedNotNull { index, value ->
                        if (value == null || value.isNaN()) null else entryOf(index, value)
                    }
                }
                producer.setEntries(entries)
            }
        }
    }

    fun selectLap(lap: WorkoutLap?) {
        _selectedLap.value = lap
    }

    fun saveVisibleCharts(charts: List<WidgetItem>) {
        val typeName = _sessionData.value?.activityName ?: return
        viewModelScope.launch {
            settingsManager.saveVisibleCharts(typeName, charts)
        }
    }

    fun saveTrackColor(color: Color) {
        val typeName = _sessionData.value?.activityName ?: return
        viewModelScope.launch {
            settingsManager.saveTrackColor(typeName, color.toArgb())
        }
    }
    
    fun clearError() {
        _error.value = null
    }
}
