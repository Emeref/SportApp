package com.example.sportapp.presentation.activities

import android.content.Context
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sportapp.data.SessionData
import com.example.sportapp.data.SessionRepository
import com.example.sportapp.data.db.WorkoutDao
import com.example.sportapp.data.model.HeartRateZoneResult
import com.example.sportapp.presentation.settings.MobileSettingsManager
import com.example.sportapp.presentation.settings.MobileSettingsState
import com.example.sportapp.presentation.stats.ActivityDetailSettings
import com.example.sportapp.presentation.stats.ActivityDetailSettingsManager
import com.example.sportapp.presentation.stats.HeartRateMath
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ActivityCompareViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val sessionRepository: SessionRepository,
    private val workoutDao: WorkoutDao,
    private val mobileSettingsManager: MobileSettingsManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val id1: Long = (savedStateHandle.get<String>("id1")?.toLongOrNull())
        ?: (savedStateHandle.get<Long>("id1"))
        ?: -1L
    private val id2: Long = (savedStateHandle.get<String>("id2")?.toLongOrNull())
        ?: (savedStateHandle.get<Long>("id2"))
        ?: -1L

    private val settingsManager = ActivityDetailSettingsManager(context)

    private val _session1 = MutableStateFlow<SessionData?>(null)
    val session1 = _session1.asStateFlow()

    private val _session2 = MutableStateFlow<SessionData?>(null)
    val session2 = _session2.asStateFlow()

    private val _hrZones1 = MutableStateFlow<HeartRateZoneResult?>(null)
    val hrZones1 = _hrZones1.asStateFlow()

    private val _hrZones2 = MutableStateFlow<HeartRateZoneResult?>(null)
    val hrZones2 = _hrZones2.asStateFlow()

    private val _settings = MutableStateFlow<ActivityDetailSettings?>(null)
    val settings = _settings.asStateFlow()

    val mobileSettings: StateFlow<MobileSettingsState> = mobileSettingsManager.settingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MobileSettingsState())

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
        "przewyzszenia_dol" to ChartEntryModelProducer(),
        "pressure" to ChartEntryModelProducer()
    )

    init {
        Log.d("ChartDebug", "CompareViewModel init: id1=$id1, id2=$id2")
        loadSessions()
    }

    private fun loadSessions() {
        viewModelScope.launch {
            if (id1 == -1L || id2 == -1L) {
                _error.value = "Nieprawidłowe ID aktywności"
                Log.e("ChartDebug", "Invalid IDs in CompareViewModel: $id1, $id2")
                return@launch
            }

            try {
                val data1 = sessionRepository.getSessionData(id1)
                val data2 = sessionRepository.getSessionData(id2)

                if (data1.error != null) _error.value = data1.error
                else if (data2.error != null) _error.value = data2.error
                else {
                    _session1.value = data1
                    _session2.value = data2
                    
                    viewModelScope.launch {
                        settingsManager.getSettingsFlow(data1.activityName).collect {
                            _settings.value = it
                        }
                    }
                    
                    val settings = mobileSettingsManager.settingsFlow.first()
                    val maxHr = settings.healthData.maxHR
                    
                    val points1 = workoutDao.getPointsForWorkout(id1)
                    val points2 = workoutDao.getPointsForWorkout(id2)
                    
                    _hrZones1.value = HeartRateMath.calculateZones(points1, maxHr)
                    _hrZones2.value = HeartRateMath.calculateZones(points2, maxHr)
                    
                    updateCharts(data1, data2)
                }
            } catch (e: Exception) {
                _error.value = "Błąd: ${e.message}"
                Log.e("ChartDebug", "Exception in loadSessions", e)
            }
        }
    }

    private fun updateCharts(data1: SessionData, data2: SessionData) {
        viewModelScope.launch(Dispatchers.Default) {
            val logSummary = StringBuilder("Compare ChartUpdate summary:")
            chartProducers.forEach { (id, producer) ->
                try {
                    val c1 = data1.charts[id] ?: emptyList()
                    val c2 = data2.charts[id] ?: emptyList()
                    
                    if (c1.isNotEmpty() || c2.isNotEmpty()) {
                        // Funkcja pomocnicza do mapowania z opcjonalnym windowingiem
                        fun mapPoints(points: List<Float?>): List<com.patrykandpatrick.vico.core.entry.ChartEntry> {
                            return if (points.size >= 10 && id in listOf("bpm", "kroki_min", "wysokosc", "predkosc", "predkosc_kroki", "pressure")) {
                                points.windowed(10, 1, true) { window: List<Float?> ->
                                    val valid = window.filterNotNull()
                                    if (valid.isEmpty()) null else valid.average().toFloat()
                                }.mapIndexedNotNull { index, value ->
                                    if (value == null || value.isNaN()) null else entryOf(index, value)
                                }
                            } else {
                                points.mapIndexedNotNull { index, value ->
                                    if (value == null || value.isNaN()) null else entryOf(index, value)
                                }
                            }
                        }

                        val entries1 = mapPoints(c1)
                        val entries2 = mapPoints(c2)

                        withContext(Dispatchers.Main) {
                            producer.setEntries(listOf(entries1, entries2))
                        }
                        logSummary.append("\n  - $id: series1=${entries1.size}, series2=${entries2.size}")
                    } else {
                        withContext(Dispatchers.Main) {
                            producer.setEntries(emptyList<List<com.patrykandpatrick.vico.core.entry.ChartEntry>>())
                        }
                    }
                } catch (e: Exception) {
                    logSummary.append("\n  - $id: ERROR ${e.message}")
                }
            }
            Log.d("ChartDebug", logSummary.toString())
        }
    }
}
