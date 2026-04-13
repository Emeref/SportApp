package com.example.sportapp.presentation.workout

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.*
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.wear.ongoing.OngoingActivity
import androidx.wear.ongoing.Status
import com.example.sportapp.R
import com.example.sportapp.TextsWearPL
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
    val allPoints: List<WorkoutPointEntity> = emptyList(),
    val maxBpm: Int = 0,
    val maxSpeedGps: Double = 0.0,
    val maxSpeedSteps: Double = 0.0,
    val avgSpeedGps: Double = 0.0,
    val avgSpeedSteps: Double = 0.0
)

@AndroidEntryPoint
class WorkoutService : Service(), SensorEventListener {

    @Inject lateinit var workoutDao: WorkoutDao
    @Inject lateinit var workoutDefinitionDao: WorkoutDefinitionDao
    @Inject lateinit var dataLayerManager: DataLayerManager
    @Inject lateinit var altitudeManager: AltitudeManager

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
    private var currentDefinitionId: Long = -1
    private var totalMillisBeforePause: Long = 0L
    private var lastResumeTimeMillis: Long = 0L
    private var totalSeconds = 0L
    private var heartRate = 0f
    private var maxBpm = 0
    private var currentSteps = 0
    private var lastSensorStepCount = -1
    private var totalDistance = 0f
    private var lastLocation: Location? = null
    private var speedKmH = 0f
    private var maxSpeedGps = 0.0
    private var maxSpeedSteps = 0.0
    private var altitude: Double? = null
    private var pressureValue = 0.0
    private var healthData: HealthData? = null
    private var sportDefinition: WorkoutDefinition? = null
    private var fallbackActivityName: String = TextsWearPL.GEN_ACTIVITY
    private var totalCaloriesAcc = 0.0
    
    private val pointsList = mutableListOf<WorkoutPointEntity>()

    private var timerJob: Job? = null
    private var autosaveJob: Job? = null
    private var locationCallback: LocationCallback? = null

    private val batteryReceiver = object : BroadcastReceiver() {
        private var lastTriggeredLevel = -1
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == Intent.ACTION_BATTERY_CHANGED) {
                val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                val batteryPct = if (scale > 0) (level * 100 / scale.toFloat()).toInt() else -1
                
                // Synchronizacja tylko raz dla danego poziomu w zakresie 1-5%
                if (batteryPct in 1..5 && batteryPct != lastTriggeredLevel) {
                    lastTriggeredLevel = batteryPct
                    serviceScope.launch(Dispatchers.IO) {
                        Log.w("WorkoutService", "Emergency save and sync triggered at $batteryPct% battery")
                        performSave(isFinal = true) // Oznaczamy jako zakończony przy awaryjnym zapisie
                        dataLayerManager.syncAll()
                    }
                }
            }
        }
    }

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

        // Tworzymy kontekst z przypisaniem
        val attributionContext = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            createAttributionContext("workout_service")
        } else {
            this
        }

        // WAŻNE: Używamy attributionContext zamiast 'this' lub 'getSystemService'
        sensorManager = attributionContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(attributionContext)

        createNotificationChannel()
        registerReceiver(batteryReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) return START_STICKY
        
        when (intent.action) {
            ACTION_START -> {
                if (status != WorkoutStatus.IDLE) {
                    Log.d("WorkoutService", "Workout already in progress, ignoring ACTION_START")
                    return START_STICKY
                }

                val definitionId = intent.getLongExtra(EXTRA_DEFINITION_ID, -1L)
                currentDefinitionId = definitionId
                fallbackActivityName = intent.getStringExtra(EXTRA_ACTIVITY_NAME) ?: TextsWearPL.GEN_ACTIVITY
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
                        durationSeconds = 0,
                        autoLapDistance = definition?.autoLapDistance,
                        isFinished = false // Startujemy z false
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
        lastResumeTimeMillis = System.currentTimeMillis()
        totalMillisBeforePause = 0L
        totalSeconds = 0L
        heartRate = 0f
        maxBpm = 0
        lastSensorStepCount = -1
        currentSteps = 0
        totalDistance = 0f
        lastLocation = null
        speedKmH = 0f
        maxSpeedGps = 0.0
        maxSpeedSteps = 0.0
        altitude = null
        pressureValue = 0.0
        totalCaloriesAcc = 0.0
        pointsList.clear()
        
        altitudeManager.reset()
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
        
        setupOngoingActivity()
        
        registerSensors()
        startTimer()
        startAutosave()
        startLocationUpdates()
        updateState(null)
    }

    private fun setupOngoingActivity() {
        val intent = Intent(this, com.example.sportapp.presentation.MainActivity::class.java).apply {
            putExtra("EXTRA_DEFINITION_ID", currentDefinitionId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        val ongoingActivity = OngoingActivity.Builder(
            applicationContext, NOTIFICATION_ID, createNotificationBuilder()
        ).setStaticIcon(R.mipmap.ic_launcher)
            .setTouchIntent(pendingIntent)
            .setStatus(
                Status.Builder()
                    .addPart("name", Status.TextPart(sportDefinition?.name ?: fallbackActivityName))
                    .build()
            )
            .build()

        ongoingActivity.apply(applicationContext)
    }

    private fun togglePause() {
        val wasActive = status == WorkoutStatus.ACTIVE
        if (status == WorkoutStatus.ACTIVE) {
            status = WorkoutStatus.PAUSED
            totalMillisBeforePause += (System.currentTimeMillis() - lastResumeTimeMillis)
        } else if (status == WorkoutStatus.PAUSED) {
            status = WorkoutStatus.ACTIVE
            lastResumeTimeMillis = System.currentTimeMillis()
            // Resetuj parametry ruchu, aby nie liczyć skoku dystansu/kroków podczas pauzy
            lastLocation = null
            lastSensorStepCount = -1
        }
        updateNotification()
        updateState(null)

        // Po zapauzowaniu aktywności - tylko zapis, bez sync (aby nie wysyłać nieukończonych do HC)
        if (wasActive && status == WorkoutStatus.PAUSED) {
            serviceScope.launch {
                performSave(isFinal = false)
            }
        }
    }

    private fun stopWorkout() {
        // Natychmiastowe zatrzymanie timerów
        timerJob?.cancel()
        autosaveJob?.cancel()
        
        if (status == WorkoutStatus.ACTIVE) {
            totalMillisBeforePause += (System.currentTimeMillis() - lastResumeTimeMillis)
        }
        totalSeconds = totalMillisBeforePause / 1000
        status = WorkoutStatus.IDLE
        unregisterSensors()
        locationCallback?.let { fusedLocationClient.removeLocationUpdates(it) }
        wakeLock?.let { if (it.isHeld) it.release() }
        
        // Priorytetowe usunięcie Ongoing Activity i powiadomienia
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID)
        stopForeground(STOP_FOREGROUND_REMOVE)
        
        serviceScope.launch {
            // Wykonujemy ostatni zapis (Flush) z flagą isFinished = true
            performSave(isFinal = true)
            dataLayerManager.syncAll() // Pełna synchronizacja po zakończeniu (teraz wyśle trening na telefon)
            
            withContext(Dispatchers.Main) {
                stopSelf()
            }
        }
    }

    private suspend fun performSave(isFinal: Boolean = false) {
        if (currentWorkoutId == -1L) return
        
        withContext(Dispatchers.IO) {
            // Zapisujemy zbuforowane punkty przed obliczeniem statystyk
            logger?.flushPoints()

            val points = workoutDao.getPointsForWorkout(currentWorkoutId)
            val durationHours = totalSeconds / 3600.0
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
            val avgSpeedGps = if (durationHours > 0) (totalDistance / 1000.0) / durationHours else 0.0

            val workout = workoutDao.getWorkoutById(currentWorkoutId)
            val updatedWorkout = workout?.copy(
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
                maxCalorieMin = if (isRecording(WorkoutSensor.CALORIES_PER_MINUTE)) logger?.getMaxCalorieMin() else null,
                durationSeconds = totalSeconds,
                avgPace = sessionStats.avgPace,
                maxSpeed = sessionStats.maxSpeed,
                maxAltitude = sessionStats.maxAltitude,
                avgStepLength = sessionStats.avgStepLength,
                avgCadence = sessionStats.avgCadence,
                maxCadence = sessionStats.maxCadence,
                maxPressure = if (isRecording(WorkoutSensor.PRESSURE)) sessionStats.maxPressure else null,
                minPressure = if (isRecording(WorkoutSensor.PRESSURE)) sessionStats.minPressure else null,
                bestPace1km = sessionStats.bestPace1km,
                isSynced = false,
                isFinished = isFinal // Ustawiamy flagę zakończenia
            )
            
            if (updatedWorkout != null) {
                workoutDao.updateWorkout(updatedWorkout)
                Log.d("WorkoutService", "Workout session $currentWorkoutId saved (isFinal=$isFinal)")
            }
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
            var lastUpdateMillis = System.currentTimeMillis()
            var lastNotificationUpdateMillis = 0L
            while (isActive) {
                if (status == WorkoutStatus.ACTIVE) {
                    delay(1000)
                    val now = System.currentTimeMillis()
                    val deltaMillis = now - lastUpdateMillis
                    lastUpdateMillis = now
                    
                    val totalMillis = totalMillisBeforePause + (now - lastResumeTimeMillis)
                    totalSeconds = totalMillis / 1000
                    
                    val calorieMinNow = healthData?.let { CalorieCalculator.calculateHRR(heartRate, it) } ?: 0.0
                    totalCaloriesAcc += (calorieMinNow / 60000.0) * deltaMillis
                    
                    val lastPoint = logger?.logData(
                        durationSeconds = totalSeconds,
                        lat = lastLocation?.latitude,
                        lon = lastLocation?.longitude,
                        bpm = heartRate,
                        kroki = currentSteps,
                        gpsDystans = totalDistance,
                        predkoscGps = speedKmH,
                        wysokosc = altitude,
                        calorieMin = calorieMinNow,
                        calorieSum = totalCaloriesAcc,
                        pressure = if (pressureValue > 0) pressureValue else null,
                        accuracy = lastLocation?.accuracy
                    )
                    
                    if (lastPoint != null) {
                        pointsList.add(lastPoint)
                        if (pointsList.size > 200) pointsList.removeAt(0)
                    }
                    
                    if (heartRate > maxBpm) maxBpm = heartRate.toInt()
                    if (speedKmH > maxSpeedGps) maxSpeedGps = speedKmH.toDouble()
                    lastPoint?.speedSteps?.let { if (it > maxSpeedSteps) maxSpeedSteps = it }

                    updateState(lastPoint)

                    if (now - lastNotificationUpdateMillis > 5000) {
                        updateNotification()
                        lastNotificationUpdateMillis = now
                    }
                } else {
                    delay(500)
                    lastUpdateMillis = System.currentTimeMillis()
                }
            }
        }
    }

    private fun startAutosave() {
        autosaveJob = serviceScope.launch {
            while (isActive) {
                delay(60000) // 60 sekund
                if (status == WorkoutStatus.ACTIVE) {
                    performSave(isFinal = false)
                    // Usunięto syncAll() stąd - synchronizacja tylko po zakończeniu lub przy niskiej baterii
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
                    
                    // Kalibracja wysokości GPS dla barometru (tylko raz na start)
                    if (location.hasAltitude()) {
                        altitudeManager.setGpsAltitude(location.altitude)
                    }
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
                    val sensorSteps = it.values[0].toInt()
                    if (lastSensorStepCount != -1) {
                        val delta = sensorSteps - lastSensorStepCount
                        if (delta > 0) currentSteps += delta
                    }
                    lastSensorStepCount = sensorSteps
                }
                Sensor.TYPE_PRESSURE -> if (it.values.isNotEmpty()) {
                    pressureValue = it.values[0].toDouble()
                    // Używamy AltitudeManager do wyliczenia wysokości z offsetem GPS
                    altitude = altitudeManager.processPressure(it.values[0])
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun updateState(lastPoint: WorkoutPointEntity?) {
        val hours = totalSeconds / 3600.0
        val avgGps = if (hours > 0) (totalDistance / 1000.0) / hours else 0.0
        val distanceStepsMeters = (currentSteps * (healthData?.stepLength ?: 0).toDouble() / 100.0)
        val avgSteps = if (hours > 0) (distanceStepsMeters / 1000.0) / hours else 0.0

        _workoutState.value = WorkoutData(
            status = status,
            totalSeconds = totalSeconds,
            formattedTime = formatTime(totalSeconds),
            lastPoint = lastPoint,
            allPoints = pointsList.toList(),
            maxBpm = maxBpm,
            maxSpeedGps = maxSpeedGps,
            maxSpeedSteps = maxSpeedSteps,
            avgSpeedGps = avgGps,
            avgSpeedSteps = avgSteps
        )
    }

    private fun createNotificationBuilder(): NotificationCompat.Builder {
        val intent = Intent(this, com.example.sportapp.presentation.MainActivity::class.java).apply {
            putExtra("EXTRA_DEFINITION_ID", currentDefinitionId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("${sportDefinition?.name ?: fallbackActivityName}")
            .setContentText("${TextsWearPL.SUMMARY_DURATION}: ${formatTime(totalSeconds)}")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_WORKOUT)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
    }

    private fun createNotification(): Notification {
        return createNotificationBuilder().build()
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
        unregisterReceiver(batteryReceiver)
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
