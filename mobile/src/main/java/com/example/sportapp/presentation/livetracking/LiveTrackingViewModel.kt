package com.example.sportapp.presentation.livetracking

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sportapp.AppConstants
import com.example.sportapp.data.db.LiveLocationDao
import com.example.sportapp.data.db.LiveLocationPoint
import com.example.sportapp.data.db.WorkoutDefinitionDao
import com.example.sportapp.data.model.WorkoutDefinition
import com.example.sportapp.data.model.WorkoutSensor
import com.google.android.gms.location.*
import com.google.android.gms.wearable.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.*

@HiltViewModel
class LiveTrackingViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val liveLocationDao: LiveLocationDao,
    private val workoutDefinitionDao: WorkoutDefinitionDao
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

    private val _mapRotation = MutableStateFlow(0f)
    val mapRotation = _mapRotation.asStateFlow()

    private val _isLocked = MutableStateFlow(false)
    val isLocked = _isLocked.asStateFlow()

    private val _autoCenter = MutableStateFlow(true)
    val autoCenter = _autoCenter.asStateFlow()

    private val _isNorthOriented = MutableStateFlow(true)
    val isNorthOriented = _isNorthOriented.asStateFlow()

    private var lastBearing = 0f

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
    }

    fun setActiveDefinition(definitionId: Long) {
        viewModelScope.launch {
            _activeDefinition.value = workoutDefinitionDao.getDefinitionById(definitionId)
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
                    val dataMap = DataMapItem.fromDataItem(event.dataItem).dataMap
                    val newData = mutableMapOf<String, String>()
                    dataMap.keySet().forEach { key ->
                        newData[key] = dataMap.get<Any>(key).toString()
                    }
                    _sensorData.value = newData
                    
                    // If definition ID is sent in data map, we can update it
                    if (dataMap.containsKey("definitionId")) {
                        val defId = dataMap.getLong("definitionId")
                        if (_activeDefinition.value?.id != defId) {
                            setActiveDefinition(defId)
                        }
                    }
                }
            }
        } finally {
            dataEvents.release()
        }
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
        }
    }
}
