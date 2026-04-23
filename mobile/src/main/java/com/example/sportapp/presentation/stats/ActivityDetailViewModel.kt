package com.example.sportapp.presentation.stats

import android.content.Context
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.workDataOf
import com.example.sportapp.data.IWorkoutRepository
import com.example.sportapp.data.LapManager
import com.example.sportapp.data.SessionData
import com.example.sportapp.data.SessionRepository
import com.example.sportapp.data.db.WorkoutDao
import com.example.sportapp.data.model.WorkoutLap
import com.example.sportapp.data.model.HeartRateZoneResult
import com.example.sportapp.data.strava.StravaSyncWorker
import com.example.sportapp.healthconnect.ExerciseExportUseCase
import com.example.sportapp.healthconnect.ExportResult
import com.example.sportapp.healthconnect.HealthConnectManager
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
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ActivityDetailViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: IWorkoutRepository,
    private val sessionRepository: SessionRepository,
    private val workoutDao: WorkoutDao,
    private val lapManager: LapManager,
    private val mobileSettingsManager: MobileSettingsManager,
    private val exerciseExportUseCase: ExerciseExportUseCase,
    val healthConnectManager: HealthConnectManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val activityId: Long = (savedStateHandle.get<String>("activityId")?.toLongOrNull())
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

    private val _isExporting = MutableStateFlow(false)
    val isExporting = _isExporting.asStateFlow()

    private val _exportResult = MutableStateFlow<ExportResult?>(null)
    val exportResult = _exportResult.asStateFlow()

    val hcSessionId: StateFlow<String?> = workoutDao.getWorkoutFlowById(activityId)
        .map { it?.hcSessionId }
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val isExportedToStrava: StateFlow<Boolean> = workoutDao.getWorkoutFlowById(activityId)
        .map { it?.isExportedToStrava ?: false }
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

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
        "pressure" to ChartEntryModelProducer(),
        "avg_step_length_over_time" to ChartEntryModelProducer()
    )

    init {
        Log.d("ChartDebug", "ActivityDetail init: activityId=$activityId")
        if (activityId != -1L) {
            startDataObservation()
        }
    }

    private fun startDataObservation() {
        val workoutFlow = workoutDao.getWorkoutFlowById(activityId).filterNotNull()
        val pointsFlow = workoutDao.getPointsFlowForWorkout(activityId)
        val lapsFlow = workoutDao.getLapsFlowForWorkout(activityId)

        combine(
            workoutFlow,
            pointsFlow,
            lapsFlow,
            mobileSettingsManager.settingsFlow
        ) { workout, points, dbLaps, mSettings ->
            val effectiveAutoLapDist = workout.autoLapDistance ?: _autoLapDistance.value
            
            if (workout.autoLapDistance == null && _autoLapDistance.value == null) {
                loadAutoLapDistance(workout.activityName)
            }

            val data = sessionRepository.calculateSessionData(workout, points)
            _sessionData.value = data
            _autoLapDistance.value = effectiveAutoLapDist
            updateCharts(data)
            
            if (effectiveAutoLapDist != null && effectiveAutoLapDist > 0) {
                val generatedLaps = lapManager.processLaps(activityId, points, effectiveAutoLapDist)
                
                _laps.value = generatedLaps
                
                val hasChanges = if (generatedLaps.size != dbLaps.size) {
                    true
                } else if (generatedLaps.isNotEmpty() && dbLaps.isNotEmpty()) {
                    val lastNew = generatedLaps.last()
                    val lastOld = dbLaps.last()
                    lastNew.durationMillis != lastOld.durationMillis || 
                    lastNew.distanceMeters != lastOld.distanceMeters ||
                    lastNew.endLocationIndex != lastOld.endLocationIndex
                } else {
                    false
                }

                if (hasChanges) {
                    viewModelScope.launch(Dispatchers.IO) {
                        try {
                            workoutDao.updateLapsForWorkout(activityId, generatedLaps)
                        } catch (e: Exception) {
                            Log.e("ActivityDetail", "Błąd zapisu odcinków", e)
                        }
                    }
                }
            } else {
                _laps.value = dbLaps
            }
            
            _hrZoneResult.value = HeartRateMath.calculateZones(points, mSettings.healthData.maxHR, mSettings.language.texts)
        }
        .flowOn(Dispatchers.Default)
        .launchIn(viewModelScope)
    }

    private suspend fun loadAutoLapDistance(activityName: String) {
        val definitions = repository.getAllDefinitions().first()
        val definition = definitions.find { it.name == activityName }
        _autoLapDistance.value = definition?.autoLapDistance
    }

    private fun updateCharts(data: SessionData) {
        viewModelScope.launch(Dispatchers.Default) {
            val results = mutableMapOf<String, List<com.patrykandpatrick.vico.core.entry.ChartEntry>>()
            
            chartProducers.forEach { (id, _) ->
                try {
                    val points = data.charts[id] ?: emptyList()
                    if (points.isNotEmpty()) {
                        val base = if (points.size >= 10 && id in listOf("bpm", "kroki_min", "wysokosc", "predkosc", "predkosc_kroki", "pressure", "avg_step_length_over_time")) {
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

    fun exportActivity(id: Long, toStrava: Boolean, toHealthConnect: Boolean) {
        if (id == -1L || _isExporting.value) return
        viewModelScope.launch {
            _isExporting.value = true
            
            if (toHealthConnect) {
                val result = exerciseExportUseCase.exportActivityToHC(id)
                _exportResult.value = result
            }
            
            if (toStrava) {
                enqueueStravaSync(id)
            }
            
            _isExporting.value = false
        }
    }

    private fun enqueueStravaSync(workoutId: Long) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = OneTimeWorkRequestBuilder<StravaSyncWorker>()
            .setConstraints(constraints)
            .setInputData(workDataOf(StravaSyncWorker.EXTRA_WORKOUT_ID to workoutId))
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .addTag("StravaSync_$workoutId")
            .build()

        WorkManager.getInstance(context).enqueue(syncRequest)
    }

    fun exportToHC() {
        exportActivity(activityId, toStrava = false, toHealthConnect = true)
    }

    fun incrementHcDeniedCount() {
        viewModelScope.launch {
            mobileSettingsManager.incrementHcDeniedCount()
        }
    }

    fun resetHcDeniedCount() {
        viewModelScope.launch {
            mobileSettingsManager.resetHcDeniedCount()
        }
    }

    fun clearExportResult() {
        _exportResult.value = null
    }

    fun clearError() {
        _error.value = null
    }
}
