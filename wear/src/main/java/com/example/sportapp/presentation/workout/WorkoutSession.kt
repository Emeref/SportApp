package com.example.sportapp.presentation.workout

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.sportapp.presentation.sensors.*
import com.example.sportapp.presentation.settings.HealthData
import com.google.gson.Gson
import kotlinx.coroutines.flow.collectLatest
import java.util.*

@Composable
fun rememberWorkoutSession(
    activityName: String,
    healthData: HealthData,
    onEndWorkout: (List<Pair<String, String>>) -> Unit
): WorkoutSessionState {
    val context = LocalContext.current
    val gson = remember { Gson() }
    var service by remember { mutableStateOf<WorkoutService?>(null) }
    var workoutData by remember { mutableStateOf(WorkoutData(status = WorkoutStatus.IDLE)) }

    val connection = remember {
        object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
                val localBinder = binder as WorkoutService.LocalBinder
                service = localBinder.getService()
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                service = null
            }
        }
    }

    DisposableEffect(Unit) {
        val intent = Intent(context, WorkoutService::class.java).apply {
            action = WorkoutService.ACTION_START
            putExtra(WorkoutService.EXTRA_ACTIVITY_NAME, activityName)
            putExtra(WorkoutService.EXTRA_HEALTH_DATA_JSON, gson.toJson(healthData))
        }
        context.startForegroundService(intent)
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE)

        onDispose {
            context.unbindService(connection)
        }
    }

    LaunchedEffect(service) {
        service?.workoutState?.collectLatest {
            workoutData = it
        }
    }

    val togglePause: () -> Unit = {
        val intent = Intent(context, WorkoutService::class.java).apply {
            action = WorkoutService.ACTION_PAUSE_RESUME
        }
        context.startService(intent)
    }

    val endWorkout: () -> Unit = {
        val data = workoutData
        val summary = mutableListOf<Pair<String, String>>()
        summary.add("Czas trwania" to data.formattedTime)
        if (data.stepCount > 0) summary.add("Kroki" to "${data.stepCount}")
        if (data.totalDistance > 0) summary.add("Dystans" to String.format(Locale.US, "%.2f km", data.totalDistance / 1000.0))
        if (data.heartRate > 0) summary.add("Tętno" to "${data.heartRate.toInt()} BPM")
        if (data.totalCalories > 0) summary.add("Kalorie" to String.format(Locale.US, "%.1f kcal", data.totalCalories))
        
        onEndWorkout(summary)

        val intent = Intent(context, WorkoutService::class.java).apply {
            action = WorkoutService.ACTION_STOP
        }
        context.startService(intent)
    }

    return WorkoutSessionState(
        status = workoutData.status,
        heartRate = workoutData.heartRate,
        stepCount = workoutData.stepCount,
        distanceState = DistanceState(workoutData.totalDistance, workoutData.currentLat, workoutData.currentLon),
        speedKmH = workoutData.speedKmH,
        workoutTimerState = WorkoutTimerState(workoutData.formattedTime, workoutData.totalSeconds),
        totalCalories = workoutData.totalCalories,
        altitude = workoutData.altitude,
        togglePause = togglePause,
        endWorkout = endWorkout
    )
}

data class WorkoutSessionState(
    val status: WorkoutStatus,
    val heartRate: Float,
    val stepCount: Int,
    val distanceState: DistanceState,
    val speedKmH: Float,
    val workoutTimerState: WorkoutTimerState,
    val totalCalories: Double,
    val altitude: Double,
    val togglePause: () -> Unit,
    val endWorkout: () -> Unit
)
