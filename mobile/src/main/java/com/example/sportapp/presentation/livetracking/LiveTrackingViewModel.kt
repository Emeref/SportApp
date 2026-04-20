package com.example.sportapp.presentation.livetracking

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sportapp.AppConstants
import com.example.sportapp.data.IWorkoutRepository
import com.example.sportapp.data.db.LiveLocationDao
import com.example.sportapp.data.db.LiveLocationPoint
import com.example.sportapp.data.db.WorkoutEntity
import com.example.sportapp.data.model.WorkoutDefinition
import com.google.android.gms.location.*
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

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
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

    private val _mapRotation = MutableStateFlow(0f)
    val mapRotation = _mapRotation.asStateFlow()

    private val _isLocked = MutableStateFlow(false)
    val isLocked = _isLocked.asStateFlow()

    private val _autoCenter = MutableStateFlow(true)
    val autoCenter = _autoCenter.asStateFlow()

    private val _isNorthOriented = MutableStateFlow(true)
    val isNorthOriented = _isNorthOriented.asStateFlow()

    private var lastBearing = 0f
    private val _sessionStartTime = MutableStateFlow(0L)
    
    // Timer lokalny dla płynnego odświeżania czasu co sekundę
    private val _currentDurationSeconds = MutableStateFlow(0L)
    val formattedDuration = _currentDurationSeconds.map { formatSeconds(it) }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "00:00"
    )
    
    // Zapamiętujemy czas utworzenia ViewModelu, aby ignorować stare dane z Data Layer
    private val viewModelCreatedAt = System.currentTimeMillis()

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            val location = result.lastLocation ?: return
            _currentLocation.value = location
            
            viewModelScope.launch {
                liveLocationDao.insert(
                    LiveLocationPoint(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        timestamp = location.time,
                        bearing = if (location.hasBearing()) location.bearing else null,
                        altitude = if (location.hasAltitude()) location.altitude else null,
                        accuracy = if (location.hasAccuracy()) location.accuracy else null
                    )
                )
            }

            if (!_isNorthOriented.value) {
                updateRotation(location)
            }
        }
    }

    init {
        dataClient.addListener(this)
        startLocationUpdates()
        
        // Pętla timera lokalnego
        viewModelScope.launch {
            while (true) {
                delay(1000)
                // Inkrementujemy tylko jeśli sesja trwa i nie jest zakończona
                if (!_isFinished.value && _sessionStartTime.value != 0L) {
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
                    Log.d("LiveTrackingVM", "Detected current workout finished in DB (startTime: ${currentWorkout.startTime}), triggering popup")
                    _isFinished.value = true
                }
            }
        }

        // Sprawdzamy stan początkowy, ale ignorujemy dane starsze niż moment otwarcia ekranu
        viewModelScope.launch {
            try {
                val dataItemBuffer = dataClient.dataItems.await()
                try {
                    dataItemBuffer.forEach { item ->
                        if (item.uri.path == "/workout_data") {
                            val dataMap = DataMapItem.fromDataItem(item).dataMap
                            val timestamp = dataMap.getLong("timestamp", 0L)
                            // Jeśli dane są świeże (sprzed max 10s lub nowsze), to je procesujemy
                            if (timestamp > viewModelCreatedAt - 10000) {
                                processDataMap(dataMap)
                            } else {
                                Log.d("LiveTrackingVM", "Ignoring old data item from previous session")
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
    }

    fun setActiveDefinition(definitionId: Long) {
        viewModelScope.launch {
            _activeDefinition.value = repository.getDefinitionById(definitionId)
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000)
            .setMinUpdateIntervalMillis(1000)
            .build()
        
        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            Log.e("LiveTrackingViewModel", "Location permission missing", e)
        }
    }

    override fun onCleared() {
        super.onCleared()
        try {
            fusedLocationClient.removeLocationUpdates(locationCallback)
            dataClient.removeListener(this)
        } catch (e: Exception) {
            Log.e("LiveTrackingViewModel", "Error removing listeners", e)
        }
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
            if (_sessionStartTime.value != startTime) {
                _sessionStartTime.value = startTime
                Log.d("LiveTrackingVM", "Session startTime set to: $startTime")
            }
        }

        // Synchronizacja czasu trwania z zegarkiem (co ~5s)
        if (dataMap.containsKey("duration")) {
            val watchDuration = dataMap.getString("duration") ?: ""
            val watchSeconds = parseDurationToSeconds(watchDuration)
            // Jeśli różnica jest większa niż 2 sekundy, nadpisujemy lokalny licznik
            if (abs(watchSeconds - _currentDurationSeconds.value) > 2) {
                _currentDurationSeconds.value = watchSeconds
                Log.d("LiveTrackingVM", "Synced local timer with watch: $watchDuration (watch=$watchSeconds, local=${_currentDurationSeconds.value})")
            }
        }

        val finished = dataMap.getBoolean("isFinished", false)
        if (finished && !_isFinished.value) {
            Log.d("LiveTrackingVM", "Detected isFinished flag from Wear data map, triggering popup")
            _isFinished.value = true
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
        if (points.size < 2) return

        val lastPoint = points.last()
        val prevPoint = points[points.size - 2]

        val bearing = calculateBearing(
            prevPoint.latitude, prevPoint.longitude,
            lastPoint.latitude, lastPoint.longitude
        )

        if (abs(bearing - lastBearing) > AppConstants.MAP_ROTATION_THRESHOLD_DEGREES) {
            _mapRotation.value = -bearing // Map rotation is opposite to bearing
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

    fun toggleOrientation() {
        _isNorthOriented.value = !_isNorthOriented.value
        if (_isNorthOriented.value) {
            _mapRotation.value = 0f
        }
    }

    fun clearLiveTrackingData() {
        viewModelScope.launch {
            liveLocationDao.clear()
            _isFinished.value = false // Reset state for next session
            _sessionStartTime.value = 0L
            _currentDurationSeconds.value = 0L
        }
    }
}
