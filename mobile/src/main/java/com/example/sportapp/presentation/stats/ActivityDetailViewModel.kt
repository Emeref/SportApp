package com.example.sportapp.presentation.stats

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sportapp.data.SessionRepository
import com.example.sportapp.presentation.settings.WidgetItem
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActivityDetailViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val repository: SessionRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val sessionId: String = checkNotNull(savedStateHandle["activityId"])
    private val settingsManager = ActivityDetailSettingsManager(context)

    val settings = settingsManager.settingsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ActivityDetailSettings(
            ActivityDetailSettingsManager.DEFAULT_ELEMENTS, 
            ActivityDetailSettingsManager.DEFAULT_COLOR
        )
    )

    private val _sessionData = MutableStateFlow<com.example.sportapp.data.SessionData?>(null)
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
            val data = repository.getSessionData(sessionId)
            if (data.error != null) {
                _error.value = data.error
            } else {
                _sessionData.value = data
                updateCharts(data)
            }
        }
    }

    private fun updateCharts(data: com.example.sportapp.data.SessionData) {
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
