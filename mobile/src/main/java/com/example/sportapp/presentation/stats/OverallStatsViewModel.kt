package com.example.sportapp.presentation.stats

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sportapp.data.IWorkoutRepository
import com.example.sportapp.data.db.WorkoutEntity
import com.example.sportapp.presentation.settings.WidgetItem
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class OverallStatsViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val repository: IWorkoutRepository
) : ViewModel() {
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

    private val _startDate = MutableStateFlow<Date?>(
        Calendar.getInstance().apply { 
            add(Calendar.DAY_OF_YEAR, -7)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time
    )
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
        refreshActivityTypes()
        
        viewModelScope.launch {
            combine(_selectedType, _startDate, _endDate) { type, start, end ->
                repository.getFilteredStats(type, start, end)
            }.collect { statsMap ->
                @Suppress("UNCHECKED_CAST")
                val rawData = statsMap["raw_data"] as? List<WorkoutEntity> ?: emptyList()
                updateChartData(rawData)
                _stats.value = statsMap
            }
        }
    }

    fun refreshActivityTypes() {
        viewModelScope.launch {
            _activityTypes.value = repository.getUniqueActivityTypes()
        }
    }

    private fun updateChartData(rawData: List<WorkoutEntity>) {
        val maxDistGps = rawData.maxOfOrNull { it.distanceGps ?: 0.0 } ?: 0.0
        val maxDistSteps = rawData.maxOfOrNull { it.distanceSteps ?: 0.0 } ?: 0.0

        chartProducers["calories"]?.setEntries(rawData.mapIndexed { index, workout -> entryOf(index, workout.totalCalories?.toFloat() ?: 0f) })
        
        chartProducers["distanceGps"]?.setEntries(rawData.mapIndexed { index, workout -> 
            val value = workout.distanceGps?.toFloat() ?: 0f
            entryOf(index, if (maxDistGps > 6000) value / 1000f else value)
        })
        
        chartProducers["distanceSteps"]?.setEntries(rawData.mapIndexed { index, workout -> 
            val value = workout.distanceSteps?.toFloat() ?: 0f
            entryOf(index, if (maxDistSteps > 6000) value / 1000f else value)
        })

        chartProducers["ascent"]?.setEntries(rawData.mapIndexed { index, workout -> entryOf(index, workout.totalAscent?.toFloat() ?: 0f) })
        chartProducers["descent"]?.setEntries(rawData.mapIndexed { index, workout -> entryOf(index, workout.totalDescent?.toFloat() ?: 0f) })
        chartProducers["steps"]?.setEntries(rawData.mapIndexed { index, workout -> entryOf(index, workout.steps?.toFloat() ?: 0f) })
    }

    fun getMaxValueForWidget(id: String): Double {
        @Suppress("UNCHECKED_CAST")
        val rawData = _stats.value["raw_data"] as? List<WorkoutEntity> ?: return 0.0
        return when(id) {
            "distanceGps" -> rawData.maxOfOrNull { it.distanceGps ?: 0.0 } ?: 0.0
            "distanceSteps" -> rawData.maxOfOrNull { it.distanceSteps ?: 0.0 } ?: 0.0
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
