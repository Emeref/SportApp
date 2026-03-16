package com.example.sportapp.presentation.stats

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    val settings: StateFlow<ActivityDetailSettings> = settingsManager.getSettingsFlow(typeName).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ActivityDetailSettings(
            visibleElements = ActivityDetailSettingsManager.DEFAULT_ELEMENTS,
            trackColor = ActivityDetailSettingsManager.DEFAULT_COLOR
        )
    )

    fun saveVisibleElements(elements: List<WidgetItem>) {
        viewModelScope.launch {
            settingsManager.saveVisibleElements(typeName, elements)
        }
    }
}
