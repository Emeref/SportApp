package com.example.sportapp.presentation.livetracking

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sportapp.AppConstants
import com.example.sportapp.data.IWorkoutRepository
import com.example.sportapp.data.db.LiveLocationDao
import com.example.sportapp.data.db.LiveLocationPoint
import com.example.sportapp.data.model.WorkoutDefinition
import com.google.android.gms.wearable.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.math.*
import java.util.Locale

@HiltViewModel
class LiveTrackingViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val liveLocationDao: LiveLocationDao,
    private val repository: IWorkoutRepository
) : ViewModel(), DataClient.OnDataChangedListener {

    private val dataClient by lazy { Wearable.getDataClient(context) }

    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation = _currentLocation.asStateFlow()

    private val _routePoints = liveLocationDao.getAllPoints().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList<LiveLocationPoint>()
    )
    val routePoints = _routePoints

    private val _sensorData = MutableStateFlow<Map<String, String>>(emptyMap())
    val sensorData = _sensorData.asStateFlow()

    private val _activeDefinition = MutableStateFlow<WorkoutDefinition?>(null)
    val activeDefinition = _activeDefinition.asStateFlow()

    private val _isFinished = MutableStateFlow(false)
    val isFinished = _isFinished.asStateFlow()
    
    private val _isPaused = MutableStateFlow(false)
    val isPaused = _isPaused.asStateFlow()

    private val _mapRotation = MutableStateFlow(0f)
    val mapRotation = _mapRotation.asStateFlow()

    private val _isLocked = MutableStateFlow(false)
    val isLocked = _isLocked.asStateFlow()

    private val _autoCenter = MutableStateFlow(true)
    val autoCenter = _autoCenter.asStateFlow()

    private val _isNorthOriented = MutableStateFlow(true)
    val isNorthOriented = _isNorthOriented.asStateFlow()

    private val _zoomLevel = MutableStateFlow(17f)
    val zoomLevel = _zoomLevel.asStateFlow()

    private var lastBearing = 0f
    private val _sessionStartTime = MutableStateFlow(0L)
    
    private val _currentDurationSeconds = MutableStateFlow(0L)
    val formattedDuration = _currentDurationSeconds.map { formatSeconds(it) }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "00:00"
    )
    
    private val viewModelCreatedAt = System.currentTimeMillis()

    init {
        dataClient.addListener(this)
        
        // Pętla timera lokalnego
        viewModelScope.launch {
            while (true) {
                delay(1000)
                if (!_isFinished.value && !_isPaused.value && _sessionStartTime.value != 0L) {
                    _currentDurationSeconds.value++
                }
            }
        }
        
        // Obserwuj bazę danych ORAZ sessionStartTime w poszukiwaniu zakończenia AKTUALNEJ aktywności
        viewModelScope.launch {
            combine(_sessionStartTime, repository.getAllWorkouts()) { startTime, workouts ->
                if (startTime != 0L) {
                    workouts.find { it.startTime == startTime }
                } else null
            }.collect { currentWorkout ->
                if (currentWorkout?.isFinished == true && !_isFinished.value) {
                    Log.d("LiveTrackingVM", "Detected current workout finished in DB, stopping service")
                    _isFinished.value = true
                    stopTrackingService()
                }
            }
        }

        // Sprawdzamy stan początkowy
        viewModelScope.launch {
            try {
                val dataItemBuffer = dataClient.dataItems.await()
                try {
                    dataItemBuffer.forEach { item ->
                        if (item.uri.path == "/workout_data") {
                            val dataMap = DataMapItem.fromDataItem(item).dataMap
                            val timestamp = dataMap.getLong("timestamp", 0L)
                            if (timestamp > viewModelCreatedAt - 30000) {
                                processDataMap(dataMap)
                            }
                        }
                    }
                } finally {
                    dataItemBuffer.release()
                }
            } catch (e: Exception) {
                Log.e("LiveTrackingViewModel", "Error fetching initial state", e)
            }
        }

        // Aktualizuj currentLocation na podstawie punktów z bazy (dla orientacji mapy)
        viewModelScope.launch {
            _routePoints.collect { points ->
                if (points.isNotEmpty()) {
                    val lastPoint = points.last()
                    val location = Location("DB").apply {
                        latitude = lastPoint.latitude
                        longitude = lastPoint.longitude
                        time = lastPoint.timestamp
                        lastPoint.bearing?.let { bearing = it }
                        lastPoint.altitude?.let { altitude = it }
                        lastPoint.accuracy?.let { accuracy = it }
                    }
                    _currentLocation.value = location
                    if (!_isNorthOriented.value) {
                        updateRotation(location)
                    }
                }
            }
        }
    }

    private fun startTrackingService() {
        val intent = Intent(context, LiveTrackingService::class.java).apply {
            action = LiveTrackingService.ACTION_START
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    private fun stopTrackingService() {
        val intent = Intent(context, LiveTrackingService::class.java).apply {
            action = LiveTrackingService.ACTION_STOP
        }
        context.startService(intent)
    }

    fun setActiveDefinition(definitionId: Long) {
        viewModelScope.launch {
            _activeDefinition.value = repository.getDefinitionById(definitionId)
        }
    }

    override fun onCleared() {
        super.onCleared()
        dataClient.removeListener(this)
        // NIE zatrzymujemy serwisu tutaj, bo chcemy śledzić dalej po wyjściu z ekranu
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        try {
            dataEvents.forEach { event ->
                if (event.type == DataEvent.TYPE_CHANGED && event.dataItem.uri.path == "/workout_data") {
                    processDataMap(DataMapItem.fromDataItem(event.dataItem).dataMap)
                }
            }
        } finally {
            dataEvents.release()
        }
    }

    private fun processDataMap(dataMap: DataMap) {
        val newData = mutableMapOf<String, String>()
        dataMap.keySet().forEach { key ->
            val value = dataMap.get<Any>(key)
            if (value != null) {
                newData[key] = value.toString()
            }
        }
        _sensorData.value = newData
        
        if (dataMap.containsKey("startTime")) {
            val startTime = dataMap.getLong("startTime")
            if (_sessionStartTime.value != startTime && startTime != 0L) {
                _sessionStartTime.value = startTime
                Log.d("LiveTrackingVM", "New session started, starting foreground service")
                startTrackingService()
            }
        }

        if (dataMap.containsKey("duration")) {
            val watchDuration = dataMap.getString("duration") ?: ""
            val watchSeconds = parseDurationToSeconds(watchDuration)
            if (abs(watchSeconds - _currentDurationSeconds.value) > 2) {
                _currentDurationSeconds.value = watchSeconds
            }
        }

        val finished = dataMap.getBoolean("isFinished", false)
        if (finished && !_isFinished.value) {
            _isFinished.value = true
            stopTrackingService()
        }
        
        val status = dataMap.getString("status")
        if (status != null) {
            _isPaused.value = (status == "PAUSED")
        }

        if (dataMap.containsKey("definitionId")) {
            val defId = dataMap.getLong("definitionId")
            if (_activeDefinition.value?.id != defId) {
                setActiveDefinition(defId)
            }
        }
    }

    private fun parseDurationToSeconds(duration: String): Long {
        val parts = duration.split(":")
        return try {
            when (parts.size) {
                3 -> parts[0].toLong() * 3600 + parts[1].toLong() * 60 + parts[2].toLong()
                2 -> parts[0].toLong() * 60 + parts[1].toLong()
                else -> 0L
            }
        } catch (e: Exception) {
            0L
        }
    }

    private fun formatSeconds(seconds: Long): String {
        val h = seconds / 3600
        val m = (seconds % 3600) / 60
        val s = seconds % 60
        return if (h > 0) String.format(Locale.US, "%02d:%02d:%02d", h, m, s)
        else String.format(Locale.US, "%02d:%02d", m, s)
    }

    private fun updateRotation(location: Location) {
        val points = _routePoints.value
        if (points.isEmpty()) return

        val lastPoint = points.last()
        // Szukamy punktu sprzed ok. 5 sekund
        val targetTimestamp = lastPoint.timestamp - 5000
        val prevPoint = points.findLast { it.timestamp <= targetTimestamp } ?: points.first()

        // Jeśli nie mamy wystarczającej odległości czasowej/przestrzennej, by wyznaczyć wektor, wychodzimy
        if (prevPoint.id == lastPoint.id) return

        val bearing = calculateBearing(
            prevPoint.latitude, prevPoint.longitude,
            lastPoint.latitude, lastPoint.longitude
        )

        // Obliczamy najkrótszą różnicę kątową (uwzględniając przejście przez 0/360)
        var diff = abs(bearing - lastBearing)
        if (diff > 180) diff = 360 - diff

        if (diff > AppConstants.MAP_ROTATION_THRESHOLD_DEGREES) {
            _mapRotation.value = bearing
            lastBearing = bearing
        }
    }

    private fun calculateBearing(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val phi1 = Math.toRadians(lat1)
        val phi2 = Math.toRadians(lat2)
        val deltaLambda = Math.toRadians(lon2 - lon1)

        val y = sin(deltaLambda) * cos(phi2)
        val x = cos(phi1) * sin(phi2) - sin(phi1) * cos(phi2) * cos(deltaLambda)
        val theta = atan2(y, x)

        return ((Math.toDegrees(theta) + 360) % 360).toFloat()
    }

    fun setLocked(locked: Boolean) {
        _isLocked.value = locked
    }

    fun setAutoCenter(enabled: Boolean) {
        _autoCenter.value = enabled
    }

    fun setZoomLevel(zoom: Float) {
        _zoomLevel.value = zoom.coerceIn(2f, 21f)
    }

    fun zoomIn() {
        setZoomLevel(_zoomLevel.value + 1f)
    }

    fun zoomOut() {
        setZoomLevel(_zoomLevel.value - 1f)
    }

    fun toggleOrientation() {
        _isNorthOriented.value = !_isNorthOriented.value
        if (_isNorthOriented.value) {
            _mapRotation.value = 0f
        } else {
            // Przy przełączeniu na tryb kierunku, od razu spróbujmy ustawić właściwą rotację
            _currentLocation.value?.let { updateRotation(it) }
        }
    }

    fun clearLiveTrackingData() {
        viewModelScope.launch {
            stopTrackingService()
            liveLocationDao.clear()
            _isFinished.value = false
            _isPaused.value = false
            _sessionStartTime.value = 0L
            _currentDurationSeconds.value = 0L
        }
    }
}
