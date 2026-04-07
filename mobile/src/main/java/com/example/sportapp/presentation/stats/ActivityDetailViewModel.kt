package com.example.sportapp.presentation.stats

import android.content.Context
import android.util.Log
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
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    private val activityId: Long = (savedStateHandle.get<String>("activityId")?.toLongOrNull())
        ?: (savedStateHandle.get<Long>("activityId"))
        ?: -1L

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
        Log.d("ChartDebug", "ActivityDetail init: activityId=$activityId")
        if (activityId != -1L) {
            startDataObservation()
        }
    }

    private fun startDataObservation() {
        // 1. Obserwacja podstawowych danych trasy i treningu
        workoutDao.getWorkoutFlowById(activityId)
            .filterNotNull()
            .combine(workoutDao.getPointsFlowForWorkout(activityId)) { workout, points ->
                workout to points
            }
            .flowOn(Dispatchers.Default)
            .onEach { (workout, points) ->
                val data = sessionRepository.calculateSessionData(workout, points)
                _sessionData.value = data
                updateCharts(data)
                
                // Dynamiczne generowanie odcinków przy każdej aktualizacji punktów
                checkAndGenerateLapsReactive(points)
                
                // Aktualizacja stref tętna
                val mSettings = mobileSettingsManager.settingsFlow.first()
                _hrZoneResult.value = HeartRateMath.calculateZones(points, mSettings.healthData.maxHR)
            }
            .launchIn(viewModelScope)

        // 2. Obserwacja odcinków z bazy danych
        workoutDao.getLapsFlowForWorkout(activityId)
            .onEach { dbLaps ->
                _laps.value = dbLaps
            }
            .launchIn(viewModelScope)

        // 3. Ładowanie dystansu autolapa (tylko raz na początku lub gdy zmieni się nazwa aktywności)
        viewModelScope.launch {
            _sessionData.filterNotNull().first().let { data ->
                loadAutoLapDistance(data.activityName)
            }
        }
    }

    private suspend fun loadAutoLapDistance(activityName: String) {
        val definitions = repository.getAllDefinitions().first()
        val definition = definitions.find { it.name == activityName }
        _autoLapDistance.value = definition?.autoLapDistance
    }

    /**
     * Reaktywne sprawdzanie i generowanie odcinków.
     * Wywoływane wewnątrz flow obserwującego punkty (Dispatchers.Default).
     */
    private suspend fun checkAndGenerateLapsReactive(points: List<WorkoutPointEntity>) {
        val autoLapDist = _autoLapDistance.value ?: return
        if (autoLapDist <= 0.0) return

        val existingLaps = _laps.value
        
        // Wykorzystujemy LapManager do przyrostowego obliczenia odcinków
        val generatedLaps = lapManager.processLaps(activityId, points, autoLapDist, existingLaps)
        
        // Sprawdź czy zaszły istotne zmiany przed zapisem do bazy
        val hasChanges = if (generatedLaps.size != existingLaps.size) {
            true
        } else if (generatedLaps.isNotEmpty() && existingLaps.isNotEmpty()) {
            val lastNew = generatedLaps.last()
            val lastOld = existingLaps.last()
            // Zmiana dystansu, czasu lub indeksu końcowego (dla trwającego odcinka)
            lastNew.durationMillis != lastOld.durationMillis || 
            lastNew.distanceMeters != lastOld.distanceMeters ||
            lastNew.endLocationIndex != lastOld.endLocationIndex
        } else {
            false
        }

        if (hasChanges) {
            // Uwaga: Dla wydajności przy trwających aktywnościach, Room i tak obsłuży to asynchronicznie.
            // Używamy transakcji wewnątrz DAO (jeśli byłaby potrzebna), tutaj prosty delete/insert.
            workoutDao.deleteLapsForWorkout(activityId)
            workoutDao.insertLaps(generatedLaps)
            // _laps zostanie zaktualizowane przez flow z bazy danych
        }
    }

    private fun updateCharts(data: SessionData) {
        viewModelScope.launch(Dispatchers.Default) {
            val results = mutableMapOf<String, List<com.patrykandpatrick.vico.core.entry.ChartEntry>>()
            
            chartProducers.forEach { (id, _) ->
                try {
                    val points = data.charts[id] ?: emptyList()
                    if (points.isNotEmpty()) {
                        val base = if (points.size >= 10 && id in listOf("bpm", "kroki_min", "wysokosc", "predkosc", "predkosc_kroki", "pressure")) {
                            points.windowed(10, 1, true) { window ->
                                val valid = window.filterNotNull()
                                if (valid.isEmpty()) null else valid.average().toFloat()
                            }
                        } else points

                        results[id] = base.mapIndexedNotNull { index, value ->
                            if (value == null || value.isNaN()) null else entryOf(index, value)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("ChartDebug", "Błąd mapowania (detail) dla $id", e)
                }
            }

            withContext(Dispatchers.Main) {
                results.forEach { (id, entries) ->
                    val producer = chartProducers[id]
                    producer?.setEntries(entries)
                }
            }
        }
    }

    fun selectLap(lap: WorkoutLap?) {
        if (_selectedLap.value?.lapNumber == lap?.lapNumber) {
            _selectedLap.value = null
        } else {
            _selectedLap.value = lap
        }
    }

    fun clearError() {
        _error.value = null
    }
}
