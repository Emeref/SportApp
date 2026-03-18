package com.example.sportapp.presentation.stats

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sportapp.data.IWorkoutRepository
import com.example.sportapp.data.SessionData
import com.example.sportapp.data.SessionRepository
import com.example.sportapp.presentation.settings.WidgetItem
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ActivityDetailViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val repository: IWorkoutRepository,
    private val sessionRepository: SessionRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val activityId: Long = savedStateHandle.get<String>("activityId")?.toLongOrNull() ?: -1L
    private val settingsManager = ActivityDetailSettingsManager(context)

    private val _sessionData = MutableStateFlow<SessionData?>(null)
    val sessionData = _sessionData.asStateFlow()

    val settings: StateFlow<ActivityDetailSettings> = _sessionData
        .filterNotNull()
        .flatMapLatest { data -> 
            settingsManager.getSettingsFlow(data.activityName)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ActivityDetailSettings(
                visibleCharts = ActivityDetailSettingsManager.DEFAULT_CHARTS,
                visibleWidgets = ActivityDetailSettingsManager.DEFAULT_WIDGETS,
                trackColor = ActivityDetailSettingsManager.DEFAULT_COLOR
            )
        )

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
        loadSessionData()
    }

    private fun loadSessionData() {
        viewModelScope.launch {
            if (activityId == -1L) {
                _error.value = "Nieprawidłowe ID aktywności."
                return@launch
            }

            try {
                // Fixed: activityId is already Long, no need for toString()
                val data = sessionRepository.getSessionData(activityId)
                if (data.error != null) {
                    _error.value = data.error
                } else {
                    _sessionData.value = data
                    updateCharts(data)
                }
            } catch (e: Exception) {
                _error.value = "Błąd ładowania danych: ${e.message}"
            }
        }
    }

    private fun updateCharts(data: SessionData) {
        chartProducers.forEach { (id, producer) ->
            // Klucze w SessionData.charts są teraz zsynchronizowane z chartProducers
            val points = data.charts[id] ?: emptyList()
            if (points.isNotEmpty()) {
                producer.setEntries(points.mapIndexed { index, value ->
                    entryOf(index, value ?: 0f)
                })
            }
        }
    }

    fun saveVisibleCharts(charts: List<WidgetItem>) {
        val typeName = _sessionData.value?.activityName ?: return
        viewModelScope.launch {
            settingsManager.saveVisibleCharts(typeName, charts)
        }
    }

    fun saveTrackColor(color: Color) {
        val typeName = _sessionData.value?.activityName ?: return
        viewModelScope.launch {
            settingsManager.saveTrackColor(typeName, color.toArgb())
        }
    }
    
    fun clearError() {
        _error.value = null
    }
}
