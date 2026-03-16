package com.example.sportapp.presentation.workout

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.sportapp.data.db.WorkoutPointEntity
import com.example.sportapp.data.model.WorkoutDefinition
import com.example.sportapp.data.model.WorkoutSensor
import com.example.sportapp.presentation.sensors.*
import com.example.sportapp.presentation.settings.HealthData
import com.google.gson.Gson
import kotlinx.coroutines.flow.collectLatest
import java.util.*

@Composable
fun rememberWorkoutSession(
    activityName: String,
    healthData: HealthData,
    definitionId: Long = -1L,
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
            putExtra(WorkoutService.EXTRA_DEFINITION_ID, definitionId)
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
        
        fun isRec(sensor: WorkoutSensor): Boolean {
            val sensors = service?.getSportDefinition()?.sensors ?: return true
            return sensors.find { it.sensorId == sensor.id }?.isRecording == true
        }

        summary.add("Czas trwania" to data.formattedTime)
        
        if (lastPoint != null) {
            // Tętno -> Średnie i Maksymalne
            if (isRec(WorkoutSensor.HEART_RATE)) {
                val avgBpm = service?.getAvgBpm() ?: 0
                if (avgBpm > 0) summary.add("Średnie tętno" to "$avgBpm BPM")
                if (data.maxBpm > 0) summary.add("Maksymalne tętno" to "${data.maxBpm} BPM")
            }

            // Prędkość GPS -> Średnia i Maksymalna
            if (isRec(WorkoutSensor.SPEED_GPS)) {
                lastPoint.speedGps?.let { summary.add("Średnia prędkość" to String.format(Locale.US, "%.1f km/h", it)) }
                if (data.maxSpeedGps > 0) summary.add("Maksymalna prędkość" to String.format(Locale.US, "%.1f km/h", data.maxSpeedGps))
            }

            // Prędkość Kroki -> Średnia i Maksymalna
            if (isRec(WorkoutSensor.SPEED_STEPS)) {
                lastPoint.speedSteps?.let { summary.add("Średnia prędkość (kroki)" to String.format(Locale.US, "%.1f km/h", it)) }
                if (data.maxSpeedSteps > 0) summary.add("Maksymalna prędkość (kroki)" to String.format(Locale.US, "%.1f km/h", data.maxSpeedSteps))
            }

            // Dystanse i Kroki
            if (isRec(WorkoutSensor.DISTANCE_GPS)) {
                lastPoint.distanceGps?.let { summary.add("Dystans" to String.format(Locale.US, "%.2f km", it / 1000.0)) }
            }
            if (isRec(WorkoutSensor.DISTANCE_STEPS)) {
                lastPoint.distanceSteps?.let { summary.add("Dystans (kroki)" to "$it m") }
            }
            if (isRec(WorkoutSensor.STEPS)) {
                lastPoint.steps?.let { summary.add("Kroki" to "$it") }
            }

            // Przewyższenia
            if (isRec(WorkoutSensor.TOTAL_ASCENT)) {
                lastPoint.totalAscent?.let { if (it > 0) summary.add("W sumie w górę" to String.format(Locale.US, "%.0f m", it)) }
            }
            if (isRec(WorkoutSensor.TOTAL_DESCENT)) {
                lastPoint.totalDescent?.let { if (it > 0) summary.add("W sumie do dołu" to String.format(Locale.US, "%.0f m", it)) }
            }

            // Kalorie
            if (isRec(WorkoutSensor.CALORIES_SUM)) {
                lastPoint.calorieSum?.let { summary.add("Kalorie" to String.format(Locale.US, "%.0f kcal", it)) }
            }
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
