package com.example.sportapp.presentation.workout

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.*
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.sportapp.R
import com.example.sportapp.data.db.WorkoutDao
import com.example.sportapp.data.db.WorkoutDefinitionDao
import com.example.sportapp.data.db.WorkoutEntity
import com.example.sportapp.data.db.WorkoutPointEntity
import com.example.sportapp.data.model.WorkoutDefinition
import com.example.sportapp.data.model.WorkoutSensor
import com.example.sportapp.presentation.sensors.*
import com.example.sportapp.presentation.settings.HealthData
import com.google.android.gms.location.*
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*
import javax.inject.Inject

data class WorkoutData(
    val status: WorkoutStatus = WorkoutStatus.IDLE,
    val totalSeconds: Long = 0L,
    val formattedTime: String = "00:00",
    val lastPoint: WorkoutPointEntity? = null,
    val maxBpm: Int = 0,
    val maxSpeedGps: Double = 0.0,
    val maxSpeedSteps: Double = 0.0
)

@AndroidEntryPoint
class WorkoutService : Service(), SensorEventListener {

    @Inject lateinit var workoutDao: WorkoutDao
    @Inject lateinit var workoutDefinitionDao: WorkoutDefinitionDao
    @Inject lateinit var dataLayerManager: DataLayerManager

    private val binder = LocalBinder()
    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val gson = Gson()
    
    private lateinit var sensorManager: SensorManager
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var wakeLock: PowerManager.WakeLock? = null
    private var logger: WorkoutLogger? = null
    
    private var _workoutState = MutableStateFlow<WorkoutData>(WorkoutData())
    val workoutState = _workoutState.asStateFlow()

    private var status = WorkoutStatus.IDLE
    private var currentWorkoutId: Long = -1
    private var totalSeconds = 0L
    private var heartRate = 0f
    private var maxBpm = 0
    private var currentSteps = 0
    private var stepCountStart = -1
    private var totalDistance = 0f
    private var lastLocation: Location? = null
    private var speedKmH = 0f
    private var maxSpeedGps = 0.0
    private var maxSpeedSteps = 0.0
    private var altitude = 0.0
    private var healthData: HealthData? = null
    private var sportDefinition: WorkoutDefinition? = null
    private var fallbackActivityName: String = "Aktywność"
    private var totalCaloriesAcc = 0.0

    private var timerJob: Job? = null
    private var locationCallback: LocationCallback? = null

    fun getSportDefinition(): WorkoutDefinition? = sportDefinition

    fun getAvgBpm(): Int = logger?.getAvgBpm() ?: 0

    private fun isRecording(sensor: WorkoutSensor): Boolean {
        return sportDefinition?.sensors?.find { it.sensorId == sensor.id }?.isRecording == true
    }

    inner class LocalBinder : Binder() {
        fun getService(): WorkoutService = this@WorkoutService
    }

    override fun onBind(intent: Intent): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) return START_STICKY
        
        when (intent.action) {
            ACTION_START -> {
                val definitionId = intent.getLongExtra(EXTRA_DEFINITION_ID, -1L)
                fallbackActivityName = intent.getStringExtra(EXTRA_ACTIVITY_NAME) ?: "Aktywność"
                val hDataJson = intent.getStringExtra(EXTRA_HEALTH_DATA_JSON)
                val hData = if (hDataJson != null) gson.fromJson(hDataJson, HealthData::class.java) else HealthData()
                
                serviceScope.launch {
                    val definition = if (definitionId != -1L) workoutDefinitionDao.getDefinitionById(definitionId) else null
                    sportDefinition = definition
                    
                    val workout = WorkoutEntity(
                        activityName = definition?.name ?: fallbackActivityName,
                        startTime = System.currentTimeMillis(),
                        durationFormatted = "00:00",
                        steps = 0,
                        distanceSteps = 0.0,
                        distanceGps = 0.0,
                        avgSpeedSteps = 0.0,
                        avgSpeedGps = 0.0,
                        totalAscent = 0.0,
                        totalDescent = 0.0,
                        avgBpm = 0.0,
                        maxBpm = 0,
                        totalCalories = 0.0,
                        maxCalorieMin = 0.0,
                        durationSeconds = 0
                    )
                    currentWorkoutId = workoutDao.insertWorkout(workout)
                    withContext(Dispatchers.Main) {
                        startWorkout(hData)
                    }
                }
            }
            ACTION_PAUSE_RESUME -> togglePause()
            ACTION_STOP -> stopWorkout()
        }
        return START_STICKY
    }

    private fun startWorkout(hData: HealthData) {
        if (status != WorkoutStatus.IDLE) return
        
        this.healthData = hData
        status = WorkoutStatus.ACTIVE
        totalSeconds = 0L
        heartRate = 0f
        maxBpm = 0
        stepCountStart = -1
        currentSteps = 0
        totalDistance = 0f
        lastLocation = null
        speedKmH = 0f
        maxSpeedGps = 0.0
        maxSpeedSteps = 0.0
        altitude = 0.0
        totalCaloriesAcc = 0.0
        
        logger = WorkoutLogger(workoutDao, currentWorkoutId, hData, sportDefinition?.sensors ?: emptyList())
        
        wakeLock = (getSystemService(POWER_SERVICE) as PowerManager).newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK, "SportApp:WorkoutWakeLock"
        ).apply { acquire(10*60*60*1000L) }

        val notification = createNotification()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH or ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
        
        registerSensors()
        startTimer()
        startLocationUpdates()
        updateState(null)
    }

    private fun togglePause() {
        if (status == WorkoutStatus.ACTIVE) {
            status = WorkoutStatus.PAUSED
        } else if (status == WorkoutStatus.PAUSED) {
            status = WorkoutStatus.ACTIVE
        }
        updateNotification()
        updateState(null)
    }

    private fun stopWorkout() {
        status = WorkoutStatus.IDLE
        unregisterSensors()
        timerJob?.cancel()
        locationCallback?.let { fusedLocationClient.removeLocationUpdates(it) }
        wakeLock?.let { if (it.isHeld) it.release() }
        
        serviceScope.launch {
            val points = workoutDao.getPointsForWorkout(currentWorkoutId)
            val durationHours = totalSeconds / 3600.0
            val distanceKm = totalDistance / 1000.0
            val distanceStepsMeters = (currentSteps * (healthData?.stepLength ?: 0).toDouble() / 100.0)
            
            val officialDistanceMeters = if (totalDistance > 0) totalDistance.toDouble() else distanceStepsMeters
            
            val sessionStats = WorkoutMath.calculateSessionStats(
                points = points,
                durationSeconds = totalSeconds,
                totalDistanceMeters = officialDistanceMeters,
                totalSteps = currentSteps,
                totalDistanceGpsMeters = totalDistance.toDouble()
            )
            
            val avgSpeedSteps = if (durationHours > 0) (distanceStepsMeters / 1000.0) / durationHours else 0.0
            val avgSpeedGps = if (durationHours > 0) distanceKm / durationHours else 0.0

            val workout = workoutDao.getWorkoutById(currentWorkoutId)
            val finalWorkout = workout?.copy(
                durationFormatted = formatTime(totalSeconds),
                steps = if (isRecording(WorkoutSensor.STEPS)) currentSteps else null,
                distanceSteps = if (isRecording(WorkoutSensor.DISTANCE_STEPS)) distanceStepsMeters else null,
                distanceGps = if (isRecording(WorkoutSensor.DISTANCE_GPS)) totalDistance.toDouble() else null,
                avgSpeedSteps = if (isRecording(WorkoutSensor.SPEED_STEPS)) avgSpeedSteps else null,
                avgSpeedGps = if (isRecording(WorkoutSensor.SPEED_GPS)) avgSpeedGps else null,
                totalAscent = if (isRecording(WorkoutSensor.TOTAL_ASCENT)) sessionStats.totalAscent else null,
                totalDescent = if (isRecording(WorkoutSensor.TOTAL_DESCENT)) sessionStats.totalDescent else null,
                avgBpm = if (isRecording(WorkoutSensor.HEART_RATE)) sessionStats.avgHr.toDouble() else null,
                maxBpm = if (isRecording(WorkoutSensor.HEART_RATE)) sessionStats.maxHr else null,
                totalCalories = if (isRecording(WorkoutSensor.CALORIES_SUM)) totalCaloriesAcc else null,
                maxCalorieMin = if (isRecording(WorkoutSensor.CALORIES_PER_MINUTE)) (points.mapNotNull { it.calorieMin }.maxOrNull() ?: 0.0) else null,
                durationSeconds = totalSeconds,
                // Nowe pola
                avgPace = sessionStats.avgPace,
                maxSpeed = sessionStats.maxSpeed,
                maxAltitude = sessionStats.maxAltitude,
                avgStepLength = sessionStats.avgStepLength,
                avgCadence = sessionStats.avgCadence,
                maxCadence = sessionStats.maxCadence
            )
            
            if (finalWorkout != null) {
                workoutDao.updateWorkout(finalWorkout)
                dataLayerManager.syncActivities()
            }
            
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
    }

    private fun registerSensors() {
        sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    private fun unregisterSensors() {
        sensorManager.unregisterListener(this)
    }

    private fun startTimer() {
        timerJob = serviceScope.launch {
            while (isActive) {
                if (status == WorkoutStatus.ACTIVE) {
                    delay(1000)
                    totalSeconds++
                    
                    val calorieMinNow = healthData?.let { CalorieCalculator.calculateHRR(heartRate, it) } ?: 0.0
                    totalCaloriesAcc += (calorieMinNow / 60.0)
                    
                    val lastPoint = logger?.logData(
                        lat = lastLocation?.latitude,
                        lon = lastLocation?.longitude,
                        bpm = heartRate,
                        kroki = currentSteps,
                        gpsDystans = totalDistance,
                        predkoscGps = speedKmH,
                        wysokosc = altitude,
                        calorieMin = calorieMinNow,
                        calorieSum = totalCaloriesAcc
                    )
                    
                    // Track aggregates for UI summary
                    if (heartRate > maxBpm) maxBpm = heartRate.toInt()
                    if (speedKmH > maxSpeedGps) maxSpeedGps = speedKmH.toDouble()
                    lastPoint?.speedSteps?.let { if (it > maxSpeedSteps) maxSpeedSteps = it }

                    updateState(lastPoint)
                } else {
                    delay(500)
                }
            }
        }
    }

    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3000)
            .setMinUpdateDistanceMeters(2f)
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                if (status != WorkoutStatus.ACTIVE) return
                for (location in result.locations) {
                    lastLocation?.let { totalDistance += it.distanceTo(location) }
                    lastLocation = location
                    speedKmH = location.speed * 3.6f
                }
            }
        }

        try {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback!!, Looper.getMainLooper())
        } catch (e: SecurityException) {
            Log.e("WorkoutService", "No location permission", e)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (status != WorkoutStatus.ACTIVE) return
        event?.let {
            when (it.sensor.type) {
                Sensor.TYPE_HEART_RATE -> if (it.values.isNotEmpty()) heartRate = it.values[0]
                Sensor.TYPE_STEP_COUNTER -> if (it.values.isNotEmpty()) {
                    val steps = it.values[0].toInt()
                    if (stepCountStart == -1) stepCountStart = steps
                    currentSteps = steps - stepCountStart
                }
                Sensor.TYPE_PRESSURE -> if (it.values.isNotEmpty()) {
                    altitude = SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, it.values[0]).toDouble()
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun updateState(lastPoint: WorkoutPointEntity?) {
        _workoutState.value = WorkoutData(
            status = status,
            totalSeconds = totalSeconds,
            formattedTime = formatTime(totalSeconds),
            lastPoint = lastPoint,
            maxBpm = maxBpm,
            maxSpeedGps = maxSpeedGps,
            maxSpeedSteps = maxSpeedSteps
        )
    }

    private fun createNotification(): Notification {
        val intent = Intent(this, com.example.sportapp.presentation.MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Aktywny trening: ${sportDefinition?.name ?: fallbackActivityName}")
            .setContentText("Czas: ${formatTime(totalSeconds)}")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    private fun updateNotification() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, createNotification())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(CHANNEL_ID, "Workout Service Channel", NotificationManager.IMPORTANCE_LOW)
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(serviceChannel)
        }
    }

    private fun formatTime(seconds: Long): String {
        val h = seconds / 3600
        val m = (seconds % 3600) / 60
        val s = seconds % 60
        return if (h > 0) String.format(Locale.US, "%02d:%02d:%02d", h, m, s)
        else String.format(Locale.US, "%02d:%02d", m, s)
    }

    override fun onDestroy() {
        serviceScope.cancel()
        wakeLock?.let { if (it.isHeld) it.release() }
        super.onDestroy()
    }

    companion object {
        const val CHANNEL_ID = "WorkoutServiceChannel"
        const val NOTIFICATION_ID = 1
        const val ACTION_START = "ACTION_START"
        const val ACTION_PAUSE_RESUME = "ACTION_PAUSE_RESUME"
        const val ACTION_STOP = "ACTION_STOP"
        const val EXTRA_ACTIVITY_NAME = "EXTRA_ACTIVITY_NAME"
        const val EXTRA_DEFINITION_ID = "EXTRA_DEFINITION_ID"
        const val EXTRA_HEALTH_DATA_JSON = "EXTRA_HEALTH_DATA_JSON"
    }
}
