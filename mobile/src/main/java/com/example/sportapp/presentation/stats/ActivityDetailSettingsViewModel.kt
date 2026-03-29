package com.example.sportapp.presentation.stats

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sportapp.core.i18n.AppStrings
import com.example.sportapp.presentation.settings.WidgetItem
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActivityDetailSettingsViewModel @Inject constructor(
    @ApplicationContext context: Context,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val settingsManager = ActivityDetailSettingsManager(context)
    val typeName: String = savedStateHandle.get<String>("typeName") ?: "Other"

    fun getSettings(strings: AppStrings): StateFlow<ActivityDetailSettings> = settingsManager.getSettingsFlow(typeName, strings).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ActivityDetailSettings(
            visibleCharts = ActivityDetailSettingsManager.getDefaultCharts(strings),
            visibleWidgets = ActivityDetailSettingsManager.getDefaultWidgets(strings),
            trackColor = ActivityDetailSettingsManager.DEFAULT_COLOR
        )
    )

    fun saveVisibleCharts(charts: List<WidgetItem>) {
        viewModelScope.launch {
            settingsManager.saveVisibleCharts(typeName, charts)
        }
    }

    fun saveVisibleWidgets(widgets: List<WidgetItem>) {
        viewModelScope.launch {
            settingsManager.saveVisibleWidgets(typeName, widgets)
        }
    }
}
