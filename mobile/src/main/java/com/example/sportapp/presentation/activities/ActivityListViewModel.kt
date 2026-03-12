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

    private val _allActivities = MutableStateFlow<List<ActivityItem>>(emptyList())
    
    val activities = combine(_allActivities, _selectedType, _startDate, _endDate) { list, type, start, end ->
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        list.filter { activity ->
            val typeMatch = type == null || activity.type == type
            val dateMatch = try {
                val date = sdf.parse(activity.date)
                (start == null || date?.after(start) == true) && (end == null || date?.before(end) == true)
            } catch (e: Exception) { true }
            typeMatch && dateMatch
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        refreshActivityTypes()
        refreshActivities()
    }

    fun refreshActivityTypes() {
        viewModelScope.launch {
            _activityTypes.value = repository.getUniqueActivityTypes()
        }
    }

    fun refreshActivities() {
        viewModelScope.launch {
            _allActivities.value = repository.getActivityItems()
        }
    }

    fun onTypeSelected(type: String?) {
        _selectedType.value = if (type == "Wszystkie") null else type
    }

    fun onDateRangeSelected(start: Date?, end: Date?) {
        _startDate.value = start
        _endDate.value = end
    }
}
