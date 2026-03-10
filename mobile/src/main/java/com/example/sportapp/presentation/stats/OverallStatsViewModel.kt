package com.example.sportapp.presentation.stats

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sportapp.data.WorkoutRepository
import com.example.sportapp.presentation.settings.WidgetItem
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class OverallStatsViewModel(context: Context) : ViewModel() {
    private val repository = WorkoutRepository(context)
    private val settingsManager = OverallStatsSettingsManager(context)

    val widgets = settingsManager.widgetsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _activityTypes = MutableStateFlow<List<String>>(emptyList())
    val activityTypes = _activityTypes.asStateFlow()

    private val _selectedType = MutableStateFlow<String?>(null)
    val selectedType = _selectedType.asStateFlow()

    private val _startDate = MutableStateFlow<Date?>(null)
    val startDate = _startDate.asStateFlow()

    private val _endDate = MutableStateFlow<Date?>(null)
    val endDate = _endDate.asStateFlow()

    private val _stats = MutableStateFlow<Map<String, Any>>(emptyMap())
    val stats = _stats.asStateFlow()

    val chartProducers: Map<String, ChartEntryModelProducer> = mapOf(
        "calories" to ChartEntryModelProducer(),
        "distanceGps" to ChartEntryModelProducer(),
        "distanceSteps" to ChartEntryModelProducer(),
        "ascent" to ChartEntryModelProducer(),
        "descent" to ChartEntryModelProducer(),
        "steps" to ChartEntryModelProducer()
    )

    init {
        _activityTypes.value = repository.getUniqueActivityTypes()
        
        combine(_selectedType, _startDate, _endDate) { type, start, end ->
            repository.getFilteredStats(type, start, end)
        }.onEach { statsMap ->
            // Najpierw aktualizujemy dane wykresów, potem statystyki, 
            // aby UI nie próbowało renderować wykresów z nieaktualnymi (pustymi) producentami
            updateChartData(statsMap["raw_data"] as? List<Map<String, String>> ?: emptyList())
            _stats.value = statsMap
        }.launchIn(viewModelScope)
    }

    @Suppress("UNCHECKED_CAST")
    private fun updateChartData(rawData: List<Map<String, String>>) {
        val maxDistGps = rawData.maxOfOrNull { it["gps_dystans"]?.toDoubleOrNull() ?: 0.0 } ?: 0.0
        val maxDistSteps = rawData.maxOfOrNull { it["kroki_dystans"]?.toDoubleOrNull() ?: 0.0 } ?: 0.0

        chartProducers["calories"]?.setEntries(rawData.mapIndexed { index, map -> entryOf(index, map["kalorie"]?.toFloatOrNull() ?: 0f) })
        
        chartProducers["distanceGps"]?.setEntries(rawData.mapIndexed { index, map -> 
            val value = map["gps_dystans"]?.toFloatOrNull() ?: 0f
            entryOf(index, if (maxDistGps > 6000) value / 1000f else value)
        })
        
        chartProducers["distanceSteps"]?.setEntries(rawData.mapIndexed { index, map -> 
            val value = map["kroki_dystans"]?.toFloatOrNull() ?: 0f
            entryOf(index, if (maxDistSteps > 6000) value / 1000f else value)
        })

        chartProducers["ascent"]?.setEntries(rawData.mapIndexed { index, map -> entryOf(index, map["przewyzszenia_gora"]?.toFloatOrNull() ?: 0f) })
        chartProducers["descent"]?.setEntries(rawData.mapIndexed { index, map -> entryOf(index, map["przewyzszenia_dol"]?.toFloatOrNull() ?: 0f) })
        chartProducers["steps"]?.setEntries(rawData.mapIndexed { index, map -> entryOf(index, map["kroki"]?.toFloatOrNull() ?: 0f) })
    }

    fun getMaxValueForWidget(id: String): Double {
        val rawData = _stats.value["raw_data"] as? List<Map<String, String>> ?: return 0.0
        return when(id) {
            "distanceGps" -> rawData.maxOfOrNull { it["gps_dystans"]?.toDoubleOrNull() ?: 0.0 } ?: 0.0
            "distanceSteps" -> rawData.maxOfOrNull { it["kroki_dystans"]?.toDoubleOrNull() ?: 0.0 } ?: 0.0
            else -> 0.0
        }
    }

    fun onTypeSelected(type: String?) {
        _selectedType.value = if (type == "Wszystkie") null else type
    }

    fun onDateRangeSelected(start: Date?, end: Date?) {
        _startDate.value = start
        _endDate.value = end
    }
    
    fun saveWidgets(widgets: List<WidgetItem>) {
        viewModelScope.launch {
            settingsManager.saveWidgets(widgets)
        }
    }
}
