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
import com.example.sportapp.presentation.sensors.*
import com.example.sportapp.presentation.settings.HealthData
import com.google.android.gms.location.*
import com.google.gson.Gson
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*

class WorkoutService : Service(), SensorEventListener {

    private val binder = LocalBinder()
    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val gson = Gson()
    
    private lateinit var sensorManager: SensorManager
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var wakeLock: PowerManager.WakeLock? = null
    private var logger: WorkoutLogger? = null
    
    private var _workoutState = MutableStateFlow(WorkoutData())
    val workoutState = _workoutState.asStateFlow()

    private var status = WorkoutStatus.IDLE
    private var startTime = Date()
    private var totalSeconds = 0L
    private var heartRate = 0f
    private var stepCountStart = -1
    private var currentSteps = 0
    private var totalDistance = 0f
    private var lastLocation: Location? = null
    private var speedKmH = 0f
    private var altitude = 0.0
    private var totalCalories = 0.0
    private var healthData: HealthData? = null
    private var activityName: String = ""

    private var timerJob: Job? = null
    private var locationCallback: LocationCallback? = null

    inner class LocalBinder : Binder() {
        fun getService(): WorkoutService = this@WorkoutService
    }

    override fun onBind(intent: Intent): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) return START_STICKY
        
        when (intent.action) {
            ACTION_START -> {
                val name = intent.getStringExtra(EXTRA_ACTIVITY_NAME) ?: "Aktywność"
                val hDataJson = intent.getStringExtra(EXTRA_HEALTH_DATA_JSON)
                val hData = if (hDataJson != null) gson.fromJson(hDataJson, HealthData::class.java) else HealthData()
                startWorkout(name, hData)
            }
            ACTION_PAUSE_RESUME -> togglePause()
            ACTION_STOP -> stopWorkout()
        }
        return START_STICKY
    }

    private fun startWorkout(name: String, hData: HealthData) {
        if (status != WorkoutStatus.IDLE) return
        
        activityName = name
        healthData = hData
        status = WorkoutStatus.ACTIVE
        startTime = Date()
        totalSeconds = 0L
        heartRate = 0f
        stepCountStart = -1
        currentSteps = 0
        totalDistance = 0f
        lastLocation = null
        speedKmH = 0f
        altitude = 0.0
        totalCalories = 0.0
        
        logger = WorkoutLogger(this, activityName, hData)
        
        // Wakelock
        wakeLock = (getSystemService(Context.POWER_SERVICE) as PowerManager).newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK, "SportApp:WorkoutWakeLock"
        ).apply { acquire(10*60*60*1000L /* 10 hours max */) }

        // Start Foreground
        val notification = createNotification()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH or ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
        
        registerSensors()
        startTimer()
        startLocationUpdates()
        updateState()
    }

    private fun togglePause() {
        if (status == WorkoutStatus.ACTIVE) {
            status = WorkoutStatus.PAUSED
            serviceScope.launch { logger?.flush() }
        } else if (status == WorkoutStatus.PAUSED) {
            status = WorkoutStatus.ACTIVE
        }
        updateNotification()
        updateState()
    }

    private fun stopWorkout() {
        status = WorkoutStatus.IDLE
        unregisterSensors()
        timerJob?.cancel()
        locationCallback?.let { fusedLocationClient.removeLocationUpdates(it) }
        wakeLock?.let { if (it.isHeld) it.release() }
        
        serviceScope.launch {
            val stats = logger?.getFinalStats() ?: emptyMap()
            val durationHours = totalSeconds / 3600.0
            val distanceKm = totalDistance / 1000.0
            val distanceStepsMeters = (currentSteps * (healthData?.stepLength ?: 0).toDouble() / 100.0)
            
            val avgSpeedSteps = if (durationHours > 0) (distanceStepsMeters / 1000.0) / durationHours else 0.0
            val avgSpeedGps = if (durationHours > 0) distanceKm / durationHours else 0.0

            SummaryManager.saveSummary(
                context = this@WorkoutService,
                activityName = activityName,
                startTime = startTime,
                durationFormatted = formatTime(totalSeconds),
                steps = if (currentSteps > 0) currentSteps else null,
                distanceSteps = if (distanceStepsMeters > 0) distanceStepsMeters else null,
                distanceGps = if (totalDistance > 0) totalDistance else null,
                avgSpeedSteps = if (avgSpeedSteps > 0) avgSpeedSteps else null,
                avgSpeedGps = if (avgSpeedGps > 0) avgSpeedGps else null,
                totalAscent = stats["totalAscent"] as? Double ?: 0.0,
                totalDescent = stats["totalDescent"] as? Double ?: 0.0,
                avgBpm = stats["avgBpm"] as? Double,
                totalCalories = totalCalories,
                maxCalorieMin = stats["maxCalorieMin"] as? Double ?: 0.0,
                durationSeconds = totalSeconds
            )
            
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
                    
                    val calorieMin = healthData?.let { CalorieCalculator.calculateHRR(heartRate, it) } ?: 0.0
                    totalCalories += (calorieMin / 60.0)
                    
                    logger?.logData(
                        lat = lastLocation?.latitude,
                        lon = lastLocation?.longitude,
                        bpm = heartRate,
                        kroki = currentSteps,
                        gpsDystans = totalDistance,
                        predkoscGps = speedKmH,
                        wysokosc = altitude,
                        calorieMin = calorieMin,
                        calorieSum = totalCalories
                    )
                    updateState()
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
                    updateState()
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
            updateState()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun updateState() {
        _workoutState.value = WorkoutData(
            status = status,
            heartRate = heartRate,
            stepCount = currentSteps,
            totalDistance = totalDistance,
            currentLat = lastLocation?.latitude,
            currentLon = lastLocation?.longitude,
            speedKmH = speedKmH,
            totalSeconds = totalSeconds,
            formattedTime = formatTime(totalSeconds),
            totalCalories = totalCalories,
            altitude = altitude
        )
    }

    private fun createNotification(): Notification {
        val intent = Intent(this, com.example.sportapp.presentation.MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Aktywny trening: $activityName")
            .setContentText("Czas: ${formatTime(totalSeconds)}")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    private fun updateNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
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
        const val EXTRA_HEALTH_DATA_JSON = "EXTRA_HEALTH_DATA_JSON"
    }
}

data class WorkoutData(
    val status: WorkoutStatus = WorkoutStatus.IDLE,
    val heartRate: Float = 0f,
    val stepCount: Int = 0,
    val totalDistance: Float = 0f,
    val currentLat: Double? = null,
    val currentLon: Double? = null,
    val speedKmH: Float = 0f,
    val totalSeconds: Long = 0L,
    val formattedTime: String = "00:00",
    val totalCalories: Double = 0.0,
    val altitude: Double = 0.0
)
