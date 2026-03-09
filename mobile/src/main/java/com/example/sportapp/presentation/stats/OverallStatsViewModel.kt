package com.example.sportapp.presentation.stats

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sportapp.data.WorkoutRepository
import com.example.sportapp.presentation.settings.WidgetItem
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class OverallStatsViewModel(context: Context) : ViewModel() {
    private val repository = WorkoutRepository(context)
    private val settingsManager = OverallStatsSettingsManager(context)

    private val _widgets = settingsManager.widgetsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    val widgets = _widgets

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

    init {
        _activityTypes.value = repository.getUniqueActivityTypes()
        
        combine(_selectedType, _startDate, _endDate) { type, start, end ->
            repository.getFilteredStats(type, start, end)
        }.onEach { 
            _stats.value = it 
        }.launchIn(viewModelScope)
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
