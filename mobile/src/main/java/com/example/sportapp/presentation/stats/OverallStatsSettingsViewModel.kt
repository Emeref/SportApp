package com.example.sportapp.presentation.stats

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sportapp.presentation.settings.WidgetItem
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OverallStatsSettingsViewModel @Inject constructor(
    @ApplicationContext context: Context
) : ViewModel() {
    private val settingsManager = OverallStatsSettingsManager(context)

    private val _widgets = MutableStateFlow<List<WidgetItem>>(emptyList())
    val widgets: StateFlow<List<WidgetItem>> = _widgets.asStateFlow()

    private val _charts = MutableStateFlow<List<WidgetItem>>(emptyList())
    val charts: StateFlow<List<WidgetItem>> = _charts.asStateFlow()

    init {
        viewModelScope.launch {
            _widgets.value = settingsManager.widgetsFlow.first()
            _charts.value = settingsManager.chartsFlow.first()
        }
    }

    fun toggleWidget(id: String) {
        val list = _widgets.value.toMutableList()
        val index = list.indexOfFirst { it.id == id }
        if (index != -1) {
            list[index] = list[index].copy(isEnabled = !list[index].isEnabled)
            _widgets.value = list
            viewModelScope.launch {
                settingsManager.saveWidgets(list)
            }
        }
    }

    fun moveWidget(fromIndex: Int, toIndex: Int) {
        if (fromIndex !in _widgets.value.indices || toIndex !in _widgets.value.indices) return
        val list = _widgets.value.toMutableList()
        val item = list.removeAt(fromIndex)
        list.add(toIndex, item)
        _widgets.value = list
        viewModelScope.launch {
            settingsManager.saveWidgets(list)
        }
    }

    fun toggleChart(id: String) {
        val list = _charts.value.toMutableList()
        val index = list.indexOfFirst { it.id == id }
        if (index != -1) {
            list[index] = list[index].copy(isEnabled = !list[index].isEnabled)
            _charts.value = list
            viewModelScope.launch {
                settingsManager.saveCharts(list)
            }
        }
    }

    fun moveChart(fromIndex: Int, toIndex: Int) {
        if (fromIndex !in _charts.value.indices || toIndex !in _charts.value.indices) return
        val list = _charts.value.toMutableList()
        val item = list.removeAt(fromIndex)
        list.add(toIndex, item)
        _charts.value = list
        viewModelScope.launch {
            settingsManager.saveCharts(list)
        }
    }
}
