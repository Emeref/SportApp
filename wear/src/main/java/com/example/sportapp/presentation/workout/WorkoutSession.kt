package com.example.sportapp.presentation.workout

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.sportapp.data.db.WorkoutPointEntity
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
        val lastPoint = data.lastPoint
        val summary = mutableListOf<Pair<String, String>>()
        summary.add("Czas trwania" to data.formattedTime)
        
        if (lastPoint != null) {
            lastPoint.steps?.let { if (it > 0) summary.add("Kroki" to "$it") }
            lastPoint.distanceGps?.let { if (it > 0) summary.add("Dystans" to String.format(Locale.US, "%.2f km", it / 1000.0)) }
            lastPoint.bpm?.let { if (it > 0) summary.add("Tętno" to "$it BPM") }
            lastPoint.calorieSum?.let { if (it > 0) summary.add("Kalorie" to String.format(Locale.US, "%.1f kcal", it)) }
        }
        
        onEndWorkout(summary)

        val intent = Intent(context, WorkoutService::class.java).apply {
            action = WorkoutService.ACTION_STOP
        }
        context.startService(intent)
    }

    return WorkoutSessionState(
        status = workoutData.status,
        workoutTimerState = WorkoutTimerState(workoutData.formattedTime, workoutData.totalSeconds),
        lastPoint = workoutData.lastPoint,
        togglePause = togglePause,
        endWorkout = endWorkout
    )
}

data class WorkoutSessionState(
    val status: WorkoutStatus,
    val workoutTimerState: WorkoutTimerState,
    val lastPoint: WorkoutPointEntity?,
    val togglePause: () -> Unit,
    val endWorkout: () -> Unit
)
