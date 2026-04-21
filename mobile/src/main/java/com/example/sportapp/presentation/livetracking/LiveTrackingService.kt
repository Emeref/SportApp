package com.example.sportapp.presentation.livetracking

import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.location.Location
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.sportapp.MainActivity
import com.example.sportapp.R
import com.example.sportapp.data.db.LiveLocationDao
import com.example.sportapp.data.db.LiveLocationPoint
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class LiveTrackingService : Service() {

    @Inject
    lateinit var liveLocationDao: LiveLocationDao

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationCallback: LocationCallback? = null
    
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val binder = LocalBinder()

    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "live_tracking_channel"
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }

    inner class LocalBinder : Binder() {
        fun getService(): LiveTrackingService = this@LiveTrackingService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startTracking()
            ACTION_STOP -> stopTracking()
        }
        return START_STICKY
    }

    @SuppressLint("MissingPermission")
    private fun startTracking() {
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000)
            .setMinUpdateIntervalMillis(1000)
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location ->
                    saveLocation(location)
                }
            }
        }

        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback!!,
                Looper.getMainLooper()
            )
        } catch (e: Exception) {
            Log.e("LiveTrackingService", "Error requesting location updates", e)
        }
    }

    private fun saveLocation(location: Location) {
        serviceScope.launch {
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
    }

    private fun stopTracking() {
        locationCallback?.let {
            fusedLocationClient.removeLocationUpdates(it)
        }
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun createNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("SportApp")
            .setContentText("Trwa śledzenie aktywności...")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Live Tracking",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}
