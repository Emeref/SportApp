package com.example.sportapp.presentation.stats

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sportapp.data.IWorkoutRepository
import com.example.sportapp.data.db.WorkoutEntity
import com.example.sportapp.presentation.settings.WidgetItem
import com.patrykandpatrick.vico.core.entry.ChartEntry
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.Collator
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

    val charts = settingsManager.chartsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _activityTypes = MutableStateFlow<List<String>>(emptyList())
    val activityTypes = _activityTypes.asStateFlow()

    private val _selectedTypes = MutableStateFlow<Set<String>?>(null)
    val selectedTypes = _selectedTypes.asStateFlow()

    private val _startDate = MutableStateFlow<Date?>(null)
    val startDate = _startDate.asStateFlow()

    private val _endDate = MutableStateFlow<Date?>(null)
    val endDate = _endDate.asStateFlow()

    private val _stats = MutableStateFlow<Map<String, Any>>(emptyMap())
    val stats = _stats.asStateFlow()

    private val _chartMaxValues = MutableStateFlow<Map<String, Double>>(emptyMap())
    val chartMaxValues = _chartMaxValues.asStateFlow()

    val chartProducers: Map<String, ChartEntryModelProducer> = mapOf(
        "calories" to ChartEntryModelProducer(),
        "distanceGps" to ChartEntryModelProducer(),
        "distanceSteps" to ChartEntryModelProducer(),
        "ascent" to ChartEntryModelProducer(),
        "descent" to ChartEntryModelProducer(),
        "steps" to ChartEntryModelProducer(),
        "avg_cadence" to ChartEntryModelProducer(),
        "maxPressure" to ChartEntryModelProducer(),
        "minPressure" to ChartEntryModelProducer(),
        "bestPace1km" to ChartEntryModelProducer()
    )

    init {
        refreshActivityTypes()

        viewModelScope.launch {
            // Ustawienie domyślnych dat na podstawie historii aktywności
            repository.getAllWorkouts().filter { it.isNotEmpty() }.take(1).collect { workouts ->
                val oldestWorkoutDate = workouts.minOf { it.startTime }
                val oneMonthAgoCal = Calendar.getInstance().apply {
                    add(Calendar.MONTH, -1)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                val oneMonthAgo = oneMonthAgoCal.timeInMillis

                if (oldestWorkoutDate < oneMonthAgo) {
                    // Najstarsza aktywność jest starsza niż miesiąc temu -> ustawiamy filtr na miesiąc temu
                    _startDate.value = oneMonthAgoCal.time
                } else {
                    // Najstarsza aktywność jest nowsza niż miesiąc temu -> ustawiamy filtr na dzień przed pierwszą aktywnością
                    _startDate.value = Calendar.getInstance().apply {
                        timeInMillis = oldestWorkoutDate
                        add(Calendar.DAY_OF_YEAR, -1)
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }.time
                }

                if (_endDate.value == null) {
                    _endDate.value = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, 23)
                        set(Calendar.MINUTE, 59)
                        set(Calendar.SECOND, 59)
                        set(Calendar.MILLISECOND, 999)
                    }.time
                }
            }
        }
        
        viewModelScope.launch {
            combine(_selectedTypes, _startDate, _endDate, repository.getAllWorkouts()) { types, start, end, _ ->
                val typesList = types?.toList()
                val statsMap = repository.getFilteredStats(typesList, start, end)
                Triple(statsMap, start, end)
            }.collect { (statsMap, start, end) ->
                @Suppress("UNCHECKED_CAST")
                val rawData = statsMap["raw_data"] as? List<WorkoutEntity> ?: emptyList()
                
                withContext(Dispatchers.Default) {
                    updateChartData(rawData, start, end)
                }
                
                _stats.value = statsMap
            }
        }
    }

    fun refreshActivityTypes() {
        viewModelScope.launch {
            val types = repository.getUniqueActivityTypes()
            val collator = Collator.getInstance(Locale.getDefault())
            _activityTypes.value = types.sortedWith { a, b -> collator.compare(a, b) }
        }
    }

    private fun getStartOfDay(timestamp: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun updateChartData(rawData: List<WorkoutEntity>, filterStart: Date?, filterEnd: Date?) {
        if (rawData.isEmpty() && filterStart == null && filterEnd == null) {
            chartProducers.values.forEach { it.setEntries(emptyList<ChartEntry>()) }
            _chartMaxValues.value = emptyMap()
            return
        }

        val groupedByDay = rawData.groupBy { getStartOfDay(it.startTime) }
        
        val firstDate = when {
            filterStart != null -> getStartOfDay(filterStart.time)
            rawData.isNotEmpty() -> getStartOfDay(rawData.minOf { it.startTime })
            else -> getStartOfDay(System.currentTimeMillis())
        }
        
        val lastDate = when {
            filterEnd != null -> getStartOfDay(filterEnd.time)
            rawData.isNotEmpty() -> getStartOfDay(rawData.maxOf { it.startTime })
            else -> firstDate
        }

        val maxDays = 365 * 2 
        val actualFirstDate = maxOf(firstDate, lastDate - (maxDays * 86400000L))

        val dailyStats = mutableListOf<DayStats>()
        var currentDay = actualFirstDate
        val calendar = Calendar.getInstance()
        
        while (currentDay <= lastDate) {
            val dayWorkouts = groupedByDay[currentDay] ?: emptyList()
            
            val totalDistGps = dayWorkouts.sumOf { it.distanceGps ?: 0.0 }
            val totalDistSteps = dayWorkouts.sumOf { it.distanceSteps ?: 0.0 }
            val totalCalories = dayWorkouts.sumOf { it.totalCalories ?: 0.0 }
            val totalSteps = dayWorkouts.sumOf { it.steps ?: 0 }.toDouble()
            val totalAscent = dayWorkouts.sumOf { it.totalAscent ?: 0.0 }
            val totalDescent = dayWorkouts.sumOf { it.totalDescent ?: 0.0 }
            
            val maxPressure = dayWorkouts.mapNotNull { it.maxPressure }.maxOrNull() ?: 0.0
            val minPressure = dayWorkouts.mapNotNull { it.minPressure }.minOrNull() ?: 0.0
            val bestPace = dayWorkouts.mapNotNull { it.bestPace1km }.minOrNull() ?: 0.0
            
            val avgCadence = if (dayWorkouts.any { it.avgCadence != null }) {
                dayWorkouts.mapNotNull { it.avgCadence }.average()
            } else 0.0

            dailyStats.add(DayStats(currentDay, totalDistGps, totalDistSteps, totalCalories, totalSteps, totalAscent, totalDescent, maxPressure, minPressure, bestPace, avgCadence))
            
            calendar.timeInMillis = currentDay
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            currentDay = calendar.timeInMillis
        }

        val maxDailyDistGps = dailyStats.maxOfOrNull { it.distGps } ?: 0.0
        val maxDailyDistSteps = dailyStats.maxOfOrNull { it.distSteps } ?: 0.0
        
        _chartMaxValues.value = mapOf(
            "distanceGps" to maxDailyDistGps,
            "distanceSteps" to maxDailyDistSteps,
            "calories" to (dailyStats.maxOfOrNull { it.calories } ?: 0.0),
            "steps" to (dailyStats.maxOfOrNull { it.steps } ?: 0.0),
            "ascent" to (dailyStats.maxOfOrNull { it.ascent } ?: 0.0),
            "descent" to (dailyStats.maxOfOrNull { it.descent } ?: 0.0),
            "maxPressure" to (dailyStats.maxOfOrNull { it.maxPressure } ?: 0.0),
            "minPressure" to (dailyStats.maxOfOrNull { it.minPressure } ?: 0.0),
            "bestPace1km" to (dailyStats.maxOfOrNull { it.bestPace } ?: 0.0),
            "avg_cadence" to (dailyStats.maxOfOrNull { it.avgCadence } ?: 0.0)
        )

        val dayScale = 86400000L
        // POPRAWKA: Używamy liczb całkowitych dla osi X (dni od epoki) z uwzględnieniem offsetu lokalnego,
        // aby 00:00:00 czasu lokalnego trafiało dokładnie na początek dnia w UTC przy przeliczaniu w drugą stronę.
        fun safeX(ts: Long): Float {
            val offset = TimeZone.getDefault().getOffset(ts)
            return ((ts + offset) / dayScale).toFloat()
        }

        chartProducers["calories"]?.setEntries(dailyStats.map { entryOf(safeX(it.timestamp), it.calories.toFloat()) })
        chartProducers["distanceGps"]?.setEntries(dailyStats.map { 
            val value = it.distGps.toFloat()
            entryOf(safeX(it.timestamp), if (maxDailyDistGps > 6000) value / 1000f else value)
        })
        chartProducers["distanceSteps"]?.setEntries(dailyStats.map { 
            val value = it.distSteps.toFloat()
            entryOf(safeX(it.timestamp), if (maxDailyDistSteps > 6000) value / 1000f else value)
        })
        chartProducers["ascent"]?.setEntries(dailyStats.map { entryOf(safeX(it.timestamp), it.ascent.toFloat()) })
        chartProducers["descent"]?.setEntries(dailyStats.map { entryOf(safeX(it.timestamp), it.descent.toFloat()) })
        chartProducers["steps"]?.setEntries(dailyStats.map { entryOf(safeX(it.timestamp), it.steps.toFloat()) })
        chartProducers["avg_cadence"]?.setEntries(dailyStats.map { entryOf(safeX(it.timestamp), it.avgCadence.toFloat()) })
        chartProducers["maxPressure"]?.setEntries(dailyStats.map { entryOf(safeX(it.timestamp), it.maxPressure.toFloat()) })
        chartProducers["minPressure"]?.setEntries(dailyStats.map { entryOf(safeX(it.timestamp), it.minPressure.toFloat()) })
        chartProducers["bestPace1km"]?.setEntries(dailyStats.map { entryOf(safeX(it.timestamp), it.bestPace.toFloat()) })
    }

    fun getMaxValueForWidget(id: String): Double {
        return chartMaxValues.value[id] ?: 0.0
    }

    private data class DayStats(
        val timestamp: Long,
        val distGps: Double,
        val distSteps: Double,
        val calories: Double,
        val steps: Double,
        val ascent: Double,
        val descent: Double,
        val maxPressure: Double,
        val minPressure: Double,
        val bestPace: Double,
        val avgCadence: Double
    )

    fun toggleTypeSelection(type: String) {
        val all = _activityTypes.value.toSet()
        val current = _selectedTypes.value ?: all
        val newSet = if (current.contains(type)) current - type else current + type
        
        if (newSet.size == all.size) {
            _selectedTypes.value = null
        } else {
            _selectedTypes.value = newSet
        }
    }

    fun toggleAllTypes() {
        if (_selectedTypes.value == null) {
            _selectedTypes.value = emptySet()
        } else {
            _selectedTypes.value = null
        }
    }

    fun onDateRangeSelected(start: Date?, end: Date?) {
        var finalStart = start
        var finalEnd = end
        
        if (finalStart != null && finalEnd != null && finalStart.after(finalEnd)) {
            if (_startDate.value != start) {
                finalEnd = finalStart
            } else {
                finalStart = finalEnd
            }
        }
        
        _startDate.value = finalStart
        _endDate.value = finalEnd
    }
    
    fun saveWidgets(widgets: List<WidgetItem>) {
        viewModelScope.launch {
            settingsManager.saveWidgets(widgets)
        }
    }

    fun saveCharts(charts: List<WidgetItem>) {
        viewModelScope.launch {
            settingsManager.saveCharts(charts)
        }
    }
}
