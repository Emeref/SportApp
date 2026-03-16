package com.example.sportapp.presentation.activities

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sportapp.data.IWorkoutRepository
import com.example.sportapp.data.db.WorkoutEntity
import com.example.sportapp.data.db.WorkoutPointEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

data class TrimStats(
    val duration: String,
    val distanceGps: String,
    val distanceSteps: String,
    val calories: String,
    val avgBpm: String
)

@HiltViewModel
class ActivityTrimViewModel @Inject constructor(
    private val repository: IWorkoutRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val activityId: String = checkNotNull(savedStateHandle["activityId"])

    private val _workout = MutableStateFlow<WorkoutEntity?>(null)
    val workout = _workout.asStateFlow()

    private val _points = MutableStateFlow<List<WorkoutPointEntity>>(emptyList())
    val points = _points.asStateFlow()

    private val _trimRange = MutableStateFlow(0f..1f)
    val trimRange = _trimRange.asStateFlow()

    private val _previewStats = MutableStateFlow<TrimStats?>(null)
    val previewStats = _previewStats.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving = _isSaving.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val wId = activityId.toLongOrNull() ?: return@launch
            val w = repository.getWorkoutById(wId)
            if (w != null) {
                _workout.value = w
                val p = repository.getPointsForWorkout(wId)
                _points.value = p
                if (p.isNotEmpty()) {
                    _trimRange.value = 0f..(p.size - 1).toFloat()
                    updatePreview()
                }
            }
        }
    }

    fun onTrimRangeChanged(range: ClosedFloatingPointRange<Float>) {
        _trimRange.value = range
        updatePreview()
    }

    private fun updatePreview() {
        val allPoints = _points.value
        if (allPoints.isEmpty()) return

        val startIndex = _trimRange.value.start.toInt().coerceIn(0, allPoints.size - 1)
        val endIndex = _trimRange.value.endInclusive.toInt().coerceIn(0, allPoints.size - 1)
        val selectedPoints = allPoints.slice(startIndex..endIndex)

        if (selectedPoints.isEmpty()) return

        val durationSeconds = selectedPoints.size.toLong()
        
        // Stats are cumulative in points, so we need to subtract start from end for deltas
        val startPoint = selectedPoints.first()
        val endPoint = selectedPoints.last()
        
        val distGps = (endPoint.distanceGps ?: 0).toDouble() - (startPoint.distanceGps ?: 0).toDouble()
        val distSteps = (endPoint.distanceSteps ?: 0).toDouble() - (startPoint.distanceSteps ?: 0).toDouble()
        val cals = (endPoint.calorieSum ?: 0.0) - (startPoint.calorieSum ?: 0.0)
        val bpm = selectedPoints.mapNotNull { it.bpm }.average()

        _previewStats.value = TrimStats(
            duration = formatDuration(durationSeconds),
            distanceGps = repository.formatDistance(distGps.coerceAtLeast(0.0)),
            distanceSteps = repository.formatDistance(distSteps.coerceAtLeast(0.0)),
            calories = "${cals.coerceAtLeast(0.0).toInt()} kcal",
            avgBpm = if (bpm.isNaN()) "--" else "${bpm.toInt()} bpm"
        )
    }

    fun saveTrim(onComplete: () -> Unit) {
        val w = _workout.value ?: return
        val allPoints = _points.value
        if (allPoints.isEmpty()) return

        val startIndex = _trimRange.value.start.toInt().coerceIn(0, allPoints.size - 1)
        val endIndex = _trimRange.value.endInclusive.toInt().coerceIn(0, allPoints.size - 1)
        
        val startId = allPoints[startIndex].id
        val endId = allPoints[endIndex].id

        viewModelScope.launch {
            _isSaving.value = true
            repository.trimWorkout(w, startId, endId)
            _isSaving.value = false
            onComplete()
        }
    }

    private fun formatDuration(seconds: Long): String {
        val h = seconds / 3600
        val m = (seconds % 3600) / 60
        val s = seconds % 60
        return if (h > 0) String.format(Locale.US, "%02d:%02d:%02d", h, m, s)
        else String.format(Locale.US, "%02d:%02d", m, s)
    }
}
