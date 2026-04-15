package com.example.sportapp.data

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.location.Location
import android.os.*
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.sportapp.R
import com.example.sportapp.data.db.WorkoutEntity
import com.example.sportapp.data.db.WorkoutPointEntity
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*
import javax.inject.Inject

data class TrackingStats(
    val distanceMeters: Double = 0.0,
    val durationSeconds: Long = 0L,
    val currentSpeedKmH: Double = 0.0,
    val currentAltitude: Double? = null
)

@AndroidEntryPoint
class WorkoutTrackingService : Service() {

    @Inject lateinit var repository: IWorkoutRepository

    private val binder = LocalBinder()
    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var wakeLock: PowerManager.WakeLock? = null
    
    private var currentWorkoutId: Long = -1
    private var status = WorkoutStatus.IDLE
    
    private var totalDistance = 0.0
    private var totalSeconds = 0L
    private var lastLocation: Location? = null
    
    private val _trackingState = MutableStateFlow<TrackingStats>(TrackingStats())
    val trackingState = _trackingState.asStateFlow()
    
    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation = _currentLocation.asStateFlow()

    private var timerJob: Job? = null
    private var locationCallback: LocationCallback? = null

    enum class WorkoutStatus { IDLE, ACTIVE, PAUSED }

    inner class LocalBinder : Binder() {
        fun getService(): WorkoutTrackingService = this@WorkoutTrackingService
    }

    override fun onBind(intent: Intent): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.action?.let { action ->
            when (action) {
                ACTION_START -> {
                    val activityName = intent.getStringExtra(EXTRA_ACTIVITY_NAME) ?: "Activity"
                    startTracking(activityName)
                }
                ACTION_PAUSE -> pauseTracking()
                ACTION_RESUME -> resumeTracking()
                ACTION_STOP -> stopTracking()
            }
        }
        return START_STICKY
    }

    private fun startTracking(activityName: String) {
        if (status != WorkoutStatus.IDLE) return
        
        serviceScope.launch {
            val workout = WorkoutEntity(
                activityName = activityName,
                startTime = System.currentTimeMillis(),
                durationFormatted = "00:00",
                durationSeconds = 0,
                isFinished = false
            )
            currentWorkoutId = repository.insertWorkout(workout)
            
            withContext(Dispatchers.Main) {
                status = WorkoutStatus.ACTIVE
                acquireWakeLock()
                startForegroundService()
                startTimer()
                startLocationUpdates()
            }
        }
    }

    private fun pauseTracking() {
        if (status == WorkoutStatus.ACTIVE) {
            status = WorkoutStatus.PAUSED
            updateNotification()
        }
    }

    private fun resumeTracking() {
        if (status == WorkoutStatus.PAUSED) {
            status = WorkoutStatus.ACTIVE
            lastLocation = null // Avoid jump in distance
            updateNotification()
        }
    }

    private fun stopTracking() {
        status = WorkoutStatus.IDLE
        timerJob?.cancel()
        locationCallback?.let { fusedLocationClient.removeLocationUpdates(it) }
        releaseWakeLock()
        
        serviceScope.launch {
            val workout = repository.getWorkoutById(currentWorkoutId)
            workout?.let {
                repository.updateWorkout(it.copy(
                    isFinished = true,
                    durationSeconds = totalSeconds,
                    distanceGps = totalDistance,
                    durationFormatted = formatTime(totalSeconds)
                ))
            }
            withContext(Dispatchers.Main) {
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }
    }

    private fun startForegroundService() {
        val notification = createNotification()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun startTimer() {
        timerJob = serviceScope.launch {
            while (isActive) {
                delay(1000)
                if (status == WorkoutStatus.ACTIVE) {
                    totalSeconds++
                    
                    val currentLoc = _currentLocation.value
                    
                    val h = totalSeconds / 3600
                    val m = (totalSeconds % 3600) / 60
                    val s = totalSeconds % 60
                    val timeStr = String.format(Locale.US, "%02d:%02d:%02d", h, m, s)
                    
                    val point = WorkoutPointEntity(
                        workoutId = currentWorkoutId,
                        time = timeStr,
                        latitude = currentLoc?.latitude,
                        longitude = currentLoc?.longitude,
                        bpm = null,
                        steps = null,
                        stepsMin = null,
                        distanceSteps = null,
                        distanceGps = totalDistance.toInt(),
                        speedGps = currentLoc?.speed?.toDouble() ?: 0.0,
                        speedSteps = null,
                        altitude = currentLoc?.altitude,
                        horizontalAccuracy = currentLoc?.accuracy?.toDouble(),
                        totalAscent = 0.0,
                        totalDescent = 0.0,
                        calorieMin = 0.0,
                        calorieSum = 0.0
                    )
                    repository.insertPoints(listOf(point))

                    _trackingState.value = _trackingState.value.copy(
                        durationSeconds = totalSeconds,
                        distanceMeters = totalDistance
                    )

                    if (totalSeconds % 5 == 0L) {
                        updateNotification()
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
            .setMinUpdateIntervalMillis(1000)
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                if (status != WorkoutStatus.ACTIVE) return
                for (location in result.locations) {
                    lastLocation?.let { totalDistance += it.distanceTo(location) }
                    lastLocation = location
                    _currentLocation.value = location
                    _trackingState.value = _trackingState.value.copy(
                        currentSpeedKmH = location.speed * 3.6,
                        currentAltitude = if (location.hasAltitude()) location.altitude else null
                    )
                }
            }
        }

        try {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback!!, Looper.getMainLooper())
        } catch (e: Exception) {
            Log.e("TrackingService", "Error requesting location updates", e)
        }
    }

    private fun acquireWakeLock() {
        wakeLock = (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
            newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "SportApp:WorkoutTrackingWakelock").apply {
                acquire(10*60*60*1000L)
            }
        }
    }

    private fun releaseWakeLock() {
        wakeLock?.let { if (it.isHeld) it.release() }
    }

    private fun createNotification(): Notification {
        val intent = Intent(this, com.example.sportapp.MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("SportApp - Active Workout")
            .setContentText("Duration: ${formatTime(totalSeconds)}, Distance: ${String.format(Locale.US, "%.2f", totalDistance / 1000.0)} km")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_WORKOUT)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .build()
    }

    private fun updateNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, createNotification())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Workout Tracking", NotificationManager.IMPORTANCE_LOW)
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
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
        releaseWakeLock()
        super.onDestroy()
    }

    companion object {
        const val CHANNEL_ID = "WorkoutTrackingChannel"
        const val NOTIFICATION_ID = 101
        const val ACTION_START = "ACTION_START"
        const val ACTION_PAUSE = "ACTION_PAUSE"
        const val ACTION_RESUME = "ACTION_RESUME"
        const val ACTION_STOP = "ACTION_STOP"
        const val EXTRA_ACTIVITY_NAME = "EXTRA_ACTIVITY_NAME"
    }
}
