package com.example.sportapp.presentation.stats

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sportapp.data.IWorkoutRepository
import com.example.sportapp.data.SessionData
import com.example.sportapp.presentation.settings.WidgetItem
import com.google.android.gms.maps.model.LatLng
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ActivityDetailViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val repository: IWorkoutRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val activityId: Long = savedStateHandle.get<String>("activityId")?.toLongOrNull() ?: -1L
    private val settingsManager = ActivityDetailSettingsManager(context)

    val settings = settingsManager.settingsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ActivityDetailSettings(
            ActivityDetailSettingsManager.DEFAULT_ELEMENTS, 
            ActivityDetailSettingsManager.DEFAULT_COLOR
        )
    )

    private val _sessionData = MutableStateFlow<SessionData?>(null)
    val sessionData = _sessionData.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    val chartProducers: Map<String, ChartEntryModelProducer> = mapOf(
        "bpm" to ChartEntryModelProducer(),
        "kalorie_min" to ChartEntryModelProducer(),
        "kalorie_suma" to ChartEntryModelProducer(),
        "kroki_min" to ChartEntryModelProducer(),
        "odl_kroki" to ChartEntryModelProducer(),
        "predkosc_kroki" to ChartEntryModelProducer(),
        "gps_dystans" to ChartEntryModelProducer(),
        "predkosc" to ChartEntryModelProducer(),
        "wysokosc" to ChartEntryModelProducer(),
        "przewyzszenia_gora" to ChartEntryModelProducer(),
        "przewyzszenia_dol" to ChartEntryModelProducer()
    )

    init {
        loadSessionData()
    }

    private fun loadSessionData() {
        viewModelScope.launch {
            if (activityId == -1L) {
                _error.value = "Nieprawidłowe ID aktywności."
                return@launch
            }

            val workout = repository.getWorkoutById(activityId)
            if (workout == null) {
                _error.value = "Trening o ID $activityId nie został znaleziony."
                return@launch
            }

            val points = repository.getPointsForWorkout(activityId)
            
            val chartData = mutableMapOf<String, MutableList<Float?>>()
            val columnKeys = listOf("bpm", "kalorie_min", "kalorie_suma", "kroki_min", "odl_kroki", "predkosc_kroki", "gps_dystans", "predkosc", "wysokosc", "przewyzszenia_gora", "przewyzszenia_dol")
            columnKeys.forEach { chartData[it] = mutableListOf() }

            val times = mutableListOf<String>()
            val route = mutableListOf<LatLng>()

            points.forEach { p ->
                times.add(p.time)
                if (p.latitude != null && p.longitude != null) {
                    route.add(LatLng(p.latitude, p.longitude))
                }
                chartData["bpm"]?.add(p.bpm?.toFloat())
                chartData["kalorie_min"]?.add(p.calorieMin?.toFloat())
                chartData["kalorie_suma"]?.add(p.calorieSum?.toFloat())
                chartData["kroki_min"]?.add(p.stepsMin?.toFloat())
                chartData["odl_kroki"]?.add(p.distanceSteps?.toFloat())
                chartData["predkosc_kroki"]?.add(p.speedSteps?.toFloat())
                chartData["gps_dystans"]?.add(p.distanceGps?.toFloat())
                chartData["predkosc"]?.add(p.speedGps?.toFloat())
                chartData["wysokosc"]?.add(p.altitude?.toFloat())
                chartData["przewyzszenia_gora"]?.add(p.totalAscent?.toFloat())
                chartData["przewyzszenia_dol"]?.add(p.totalDescent?.toFloat())
            }

            val data = SessionData(
                times = times,
                route = route,
                charts = chartData,
                activityName = workout.activityName,
                activityDate = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(workout.startTime)),
                duration = workout.durationFormatted,
                maxBpm = workout.maxBpm ?: 0,
                avgBpm = workout.avgBpm?.toInt() ?: 0,
                totalCalories = workout.totalCalories?.toInt() ?: 0,
                maxCaloriesMin = workout.maxCalorieMin?.toFloat() ?: 0f
            )

            _sessionData.value = data
            updateCharts(data)
        }
    }

    private fun updateCharts(data: SessionData) {
        chartProducers.forEach { (id, producer) ->
            val points = data.charts[id] ?: emptyList()
            if (points.isNotEmpty()) {
                producer.setEntries(points.mapIndexed { index, value ->
                    entryOf(index, value ?: 0f)
                })
            }
        }
    }

    fun saveVisibleElements(elements: List<WidgetItem>) {
        viewModelScope.launch {
            settingsManager.saveVisibleElements(elements)
        }
    }

    fun saveTrackColor(color: Color) {
        viewModelScope.launch {
            settingsManager.saveTrackColor(color.toArgb())
        }
    }
    
    fun clearError() {
        _error.value = null
    }
}
