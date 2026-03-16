package com.example.sportapp.presentation.activities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sportapp.data.IWorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

enum class SortOrder {
    ASC, DESC
}

enum class SortColumn {
    TYPE, DATE, DURATION, CALORIES, DISTANCE_GPS, DISTANCE_STEPS
}

@HiltViewModel
class ActivityListViewModel @Inject constructor(
    private val repository: IWorkoutRepository
) : ViewModel() {

    private val _activityTypes = MutableStateFlow<List<String>>(emptyList())
    val activityTypes = _activityTypes.asStateFlow()

    private val _selectedType = MutableStateFlow<String?>(null)
    val selectedType = _selectedType.asStateFlow()

    private val _startDate = MutableStateFlow<Date?>(null)
    val startDate = _startDate.asStateFlow()

    private val _endDate = MutableStateFlow<Date?>(null)
    val endDate = _endDate.asStateFlow()

    private val _sortColumn = MutableStateFlow(SortColumn.DATE)
    val sortColumn = _sortColumn.asStateFlow()

    private val _sortOrder = MutableStateFlow(SortOrder.DESC)
    val sortOrder = _sortOrder.asStateFlow()

    val activities: StateFlow<List<ActivityItem>> = repository.getActivityItemsFlow()
        .combine(_selectedType) { all, type ->
            if (type == null) all else all.filter { it.type == type }
        }
        .combine(_startDate) { filtered, start ->
            if (start == null) filtered else filtered.filter { 
                val date = parseDate(it.date)
                date != null && (date.after(start) || isSameDay(date, start))
            }
        }
        .combine(_endDate) { filtered, end ->
            if (end == null) filtered else filtered.filter { 
                val date = parseDate(it.date)
                date != null && (date.before(end) || isSameDay(date, end))
            }
        }
        .combine(_sortColumn) { filtered, col ->
            val sorted = when (col) {
                SortColumn.TYPE -> filtered.sortedBy { it.type.lowercase() }
                SortColumn.DATE -> filtered.sortedBy { it.rawTimestamp }
                SortColumn.DURATION -> filtered.sortedBy { it.rawDurationSeconds }
                SortColumn.CALORIES -> filtered.sortedBy { it.rawCalories }
                SortColumn.DISTANCE_GPS -> filtered.sortedBy { it.rawDistanceGps }
                SortColumn.DISTANCE_STEPS -> filtered.sortedBy { it.rawDistanceSteps }
            }
            sorted
        }
        .combine(_sortOrder) { sorted, order ->
            if (order == SortOrder.DESC) sorted.reversed() else sorted
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private fun parseDate(dateStr: String): Date? {
        return try {
            SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).parse(dateStr)
        } catch (e: Exception) { null }
    }

    private fun isSameDay(d1: Date?, d2: Date?): Boolean {
        if (d1 == null || d2 == null) return false
        val cal1 = Calendar.getInstance().apply { time = d1 }
        val cal2 = Calendar.getInstance().apply { time = d2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    init {
        refreshActivityTypes()
    }

    fun refreshActivityTypes() {
        viewModelScope.launch {
            _activityTypes.value = repository.getUniqueActivityTypes()
        }
    }

    fun onTypeSelected(type: String?) {
        _selectedType.value = if (type == "Wszystkie") null else type
    }

    fun onDateRangeSelected(start: Date?, end: Date?) {
        _startDate.value = start
        _endDate.value = end
    }

    fun onSortChanged(column: SortColumn) {
        if (_sortColumn.value == column) {
            _sortOrder.value = if (_sortOrder.value == SortOrder.ASC) SortOrder.DESC else SortOrder.ASC
        } else {
            _sortColumn.value = column
            _sortOrder.value = SortOrder.DESC
        }
    }

    fun deleteActivity(id: String) {
        viewModelScope.launch {
            val workoutId = id.toLongOrNull() ?: return@launch
            val workout = repository.getWorkoutById(workoutId)
            if (workout != null) {
                repository.deleteWorkout(workout)
                refreshActivityTypes()
            }
        }
    }
}
