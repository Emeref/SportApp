package com.example.sportapp.presentation.stats

import android.content.Context
import android.util.Log
import androidx.core.content.FileProvider
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
import com.example.sportapp.data.*
import com.example.sportapp.data.db.WorkoutDao
import com.example.sportapp.data.db.WorkoutPointEntity
import com.example.sportapp.data.export.FitExporter
import com.example.sportapp.data.export.GpxExporter
import com.example.sportapp.data.model.WorkoutLap
import com.example.sportapp.data.model.HeartRateZoneResult
import com.example.sportapp.data.strava.StravaSyncWorker
import com.example.sportapp.healthconnect.ExerciseExportUseCase
import com.example.sportapp.healthconnect.ExportResult
import com.example.sportapp.healthconnect.HealthConnectManager
import com.example.sportapp.presentation.activities.ExportState
import com.example.sportapp.presentation.settings.AppMapType
import com.example.sportapp.presentation.settings.ExportFormat
import com.example.sportapp.presentation.settings.MobileSettingsManager
import com.example.sportapp.presentation.settings.MobileSettingsState
import com.google.android.gms.maps.model.LatLng
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
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
    private val exportImportManager: ExportImportManager,
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

    private val _fileExportState = MutableStateFlow<ExportState>(ExportState.Idle)
    val fileExportState = _fileExportState.asStateFlow()

    private val _selectedWidgetId = MutableStateFlow<String?>(null)
    val selectedWidgetId = _selectedWidgetId.asStateFlow()

    private val _highlightedPoint = MutableStateFlow<LatLng?>(null)
    val highlightedPoint = _highlightedPoint.asStateFlow()

    private val _highlightedSegment = MutableStateFlow<List<LatLng>>(emptyList())
    val highlightedSegment = _highlightedSegment.asStateFlow()

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
            _selectedWidgetId.value = null
            _highlightedPoint.value = null
            _highlightedSegment.value = emptyList()
        }
    }

    fun selectWidget(widgetId: String) {
        if (_selectedWidgetId.value == widgetId) {
            _selectedWidgetId.value = null
            _highlightedPoint.value = null
            _highlightedSegment.value = emptyList()
        } else {
            _selectedWidgetId.value = widgetId
            _selectedLap.value = null
            findMetricLocation(widgetId)
        }
    }

    private fun findMetricLocation(widgetId: String) {
        viewModelScope.launch(Dispatchers.Default) {
            val points = repository.getPointsForWorkout(activityId)
            if (points.isEmpty()) return@launch

            when (widgetId) {
                "max_bpm" -> {
                    val maxPoint = points.maxByOrNull { it.bpm ?: 0 }
                    updateHighlight(maxPoint)
                }
                "max_calories_min" -> {
                    val maxPoint = points.maxByOrNull { it.calorieMin ?: 0.0 }
                    updateHighlight(maxPoint)
                }
                "max_speed" -> {
                    val maxPoint = points.maxByOrNull { maxOf(it.speedGps ?: 0.0, it.speedSteps ?: 0.0) }
                    updateHighlight(maxPoint)
                }
                "max_altitude" -> {
                    val maxPoint = points.maxByOrNull { it.altitude ?: -10000.0 }
                    updateHighlight(maxPoint)
                }
                "max_cadence" -> {
                    val maxPoint = points.maxByOrNull { it.stepsMin ?: 0.0 }
                    updateHighlight(maxPoint)
                }
                "max_pressure" -> {
                    val maxPoint = points.maxByOrNull { it.pressure ?: 0.0 }
                    updateHighlight(maxPoint)
                }
                "min_pressure" -> {
                    val minPoint = points.minByOrNull { it.pressure ?: 10000.0 }
                    updateHighlight(minPoint)
                }
                "best_pace_1km" -> {
                    val segment = findBest1kmSegment(points)
                    withContext(Dispatchers.Main) {
                        _highlightedPoint.value = null
                        _highlightedSegment.value = segment
                    }
                }
                else -> {
                    withContext(Dispatchers.Main) {
                        _highlightedPoint.value = null
                        _highlightedSegment.value = emptyList()
                    }
                }
            }
        }
    }

    private suspend fun updateHighlight(point: WorkoutPointEntity?) {
        withContext(Dispatchers.Main) {
            if (point?.latitude != null && point.longitude != null) {
                _highlightedPoint.value = LatLng(point.latitude, point.longitude)
                _highlightedSegment.value = emptyList()
            } else {
                _highlightedPoint.value = null
                _highlightedSegment.value = emptyList()
            }
        }
    }

    private fun findBest1kmSegment(points: List<WorkoutPointEntity>): List<LatLng> {
        if (points.size < 2) return emptyList()
        
        var bestDuration = Long.MAX_VALUE
        var bestRange: IntRange? = null
        
        for (i in points.indices) {
            val startDist = points[i].distanceGps ?: points[i].distanceSteps ?: 0
            for (j in i + 1 until points.size) {
                val MathDist = points[j].distanceGps ?: points[j].distanceSteps ?: 0
                val deltaDist = MathDist - startDist
                
                if (deltaDist >= 1000) {
                    val startTime = parseTime(points[i].time)
                    val endTime = parseTime(points[j].time)
                    val duration = endTime - startTime
                    
                    if (duration > 0 && duration < bestDuration) {
                        bestDuration = duration
                        bestRange = i..j
                    }
                    break 
                }
            }
        }
        
        return bestRange?.let { range ->
            points.slice(range).mapNotNull { p ->
                if (p.latitude != null && p.longitude != null) LatLng(p.latitude, p.longitude) else null
            }
        } ?: emptyList()
    }

    private fun parseTime(timeStr: String): Long {
        return try {
            val sdf = SimpleDateFormat("HH:mm:ss", Locale.US)
            sdf.parse(timeStr)?.time ?: 0L
        } catch (e: Exception) {
            0L
        }
    }

    fun setMapType(mapType: AppMapType) {
        viewModelScope.launch {
            mobileSettingsManager.updateMapType(mapType)
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

    fun exportToFile(useSae: Boolean) {
        if (activityId == -1L) return
        viewModelScope.launch {
            val texts = mobileSettings.value.language.texts
            _fileExportState.value = ExportState.Exporting(0f, texts.VM_EXPORT_INITIALIZING)
            try {
                val workout = repository.getWorkoutById(activityId) ?: throw Exception("Workout not found")
                val exportDir = File(context.cacheDir, "exports")
                if (exportDir.exists()) exportDir.deleteRecursively()
                exportDir.mkdirs()

                val sdf = SimpleDateFormat("yyyyMMdd_HHmm", Locale.US)
                val baseName = "${workout.activityName}_${sdf.format(Date(workout.startTime))}"
                    .replace(" ", "_")
                    .replace(":", "")
                
                val extension = if (useSae) "sae" else "gpx"
                val fileName = "$baseName.$extension"
                val file = File(exportDir, fileName)

                if (useSae) {
                    val saeContent = exportImportManager.exportToSae(activityId)
                    file.writeText(saeContent)
                } else {
                    val gpxExporter = GpxExporter()
                    val points = repository.getPointsForWorkout(activityId)
                    val laps = workoutDao.getLapsForWorkout(activityId)
                    val gpxContent = gpxExporter.generateExport(workout, points, laps)
                    file.writeBytes(gpxContent)
                }

                val uri = FileProvider.getUriForFile(context, "com.example.sportapp.fileprovider", file)
                _fileExportState.value = ExportState.Success(uri, false)
            } catch (e: Exception) {
                _fileExportState.value = ExportState.Error(texts.vmExportError(e.message ?: "Unknown error"))
            }
        }
    }

    fun exportUriToFormat(uri: android.net.Uri, format: ExportFormat) {
        if (activityId == -1L) return
        viewModelScope.launch {
            val texts = mobileSettings.value.language.texts
            _fileExportState.value = ExportState.Exporting(0f, texts.VM_EXPORT_INITIALIZING)
            try {
                withContext(Dispatchers.IO) {
                    val workout = repository.getWorkoutById(activityId) ?: throw Exception("Workout not found")
                    val points = repository.getPointsForWorkout(activityId)
                    val laps = workoutDao.getLapsForWorkout(activityId)
                    
                    val exporter = when (format) {
                        ExportFormat.GPX -> GpxExporter()
                        ExportFormat.FIT -> FitExporter()
                    }
                    val data = exporter.generateExport(workout, points, laps)
                    
                    context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                        outputStream.write(data)
                    } ?: throw Exception("Cannot open output stream")
                }
                _fileExportState.value = ExportState.Success(uri, true)
            } catch (e: Exception) {
                _fileExportState.value = ExportState.Error(texts.vmExportError(e.message ?: "Unknown error"))
            }
        }
    }

    suspend fun getExportFileName(format: ExportFormat): String = withContext(Dispatchers.IO) {
        val workout = repository.getWorkoutById(activityId) ?: throw Exception("Workout not found")
        val sdf = SimpleDateFormat("yyyyMMdd_HHmm", Locale.US)
        val baseName = "${workout.activityName}_${sdf.format(Date(workout.startTime))}"
            .replace(" ", "_")
            .replace(":", "")
        "$baseName.${format.name.lowercase()}"
    }

    fun resetFileExportState() {
        _fileExportState.value = ExportState.Idle
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
