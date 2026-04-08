package com.example.sportapp.presentation.activities

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sportapp.MobileTexts
import com.example.sportapp.data.GpxGenerator
import com.example.sportapp.data.GpxImporter
import com.example.sportapp.data.IWorkoutRepository
import com.example.sportapp.data.ZipManager
import com.example.sportapp.data.model.WorkoutDefinition
import com.example.sportapp.presentation.settings.AppLanguage
import com.example.sportapp.presentation.settings.MobileSettingsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

enum class SortOrder {
    ASC, DESC
}

enum class SortColumn {
    TYPE, DATE, DURATION, CALORIES, DISTANCE_GPS, DISTANCE_STEPS
}

sealed class ExportState {
    object Idle : ExportState()
    data class Exporting(val progress: Float, val message: String) : ExportState()
    data class Success(val uri: Uri, val isZip: Boolean) : ExportState()
    data class Error(val message: String) : ExportState()
}

sealed class ImportState {
    object Idle : ImportState()
    object Loading : ImportState()
    data class Success(val message: String) : ImportState()
    data class Error(val message: String) : ImportState()
    data class Warning(val warnings: List<String>, val onConfirm: () -> Unit) : ImportState()
}

@HiltViewModel
class ActivityListViewModel @Inject constructor(
    private val repository: IWorkoutRepository,
    private val gpxImporter: GpxImporter,
    private val settingsManager: MobileSettingsManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _activityTypes = MutableStateFlow<List<String>>(emptyList())
    val activityTypes = _activityTypes.asStateFlow()

    private val _definitions = MutableStateFlow<List<WorkoutDefinition>>(emptyList())
    val definitions = _definitions.asStateFlow()

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

    private val _selectedIds = MutableStateFlow<Set<String>>(emptySet())
    val selectedIds = _selectedIds.asStateFlow()

    private val _exportState = MutableStateFlow<ExportState>(ExportState.Idle)
    val exportState = _exportState.asStateFlow()

    private val _importState = MutableStateFlow<ImportState>(ImportState.Idle)
    val importState = _importState.asStateFlow()

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
        viewModelScope.launch {
            repository.getAllDefinitions().collect {
                _definitions.value = it
            }
        }
    }

    fun refreshActivityTypes() {
        viewModelScope.launch {
            _activityTypes.value = repository.getUniqueActivityTypes()
        }
    }

    fun onTypeSelected(type: String?) {
        _selectedType.value = type
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

    fun toggleSelection(id: String) {
        val current = _selectedIds.value
        _selectedIds.value = if (current.contains(id)) {
            current - id
        } else {
            current + id
        }
    }

    fun toggleAllVisibleSelection() {
        val visibleActivities = activities.value
        if (visibleActivities.isEmpty()) return
        
        val visibleIds = visibleActivities.map { it.id }.toSet()
        val currentlySelected = _selectedIds.value
        val visibleSelectedIds = currentlySelected.intersect(visibleIds)
        
        if (visibleSelectedIds.isNotEmpty()) {
            _selectedIds.value = currentlySelected - visibleIds
        } else {
            _selectedIds.value = currentlySelected + visibleIds
        }
    }

    fun clearSelection() {
        _selectedIds.value = emptySet()
    }

    fun deleteSelectedActivities() {
        viewModelScope.launch {
            val idsToDelete = _selectedIds.value
            idsToDelete.forEach { id ->
                val workoutId = id.toLongOrNull() ?: return@forEach
                val workout = repository.getWorkoutById(workoutId)
                if (workout != null) {
                    repository.deleteWorkout(workout)
                }
            }
            _selectedIds.value = emptySet()
            refreshActivityTypes()
        }
    }

    fun exportSelected() {
        val ids = _selectedIds.value.toList()
        if (ids.isEmpty()) return

        viewModelScope.launch {
            val texts = getTexts()
            _exportState.value = ExportState.Exporting(0f, texts.VM_EXPORT_INITIALIZING)
            
            try {
                val gpxGenerator = GpxGenerator()
                val exportDir = File(context.cacheDir, "exports")
                if (exportDir.exists()) exportDir.deleteRecursively()
                exportDir.mkdirs()

                val generatedFiles = mutableListOf<File>()
                val sdf = SimpleDateFormat("yyyyMMdd_HHmm", Locale.US)
                val usedFileNames = mutableMapOf<String, Int>()

                ids.forEachIndexed { index, idString ->
                    val id = idString.toLongOrNull() ?: return@forEachIndexed
                    val workout = repository.getWorkoutById(id) ?: return@forEachIndexed
                    val points = repository.getPointsForWorkout(id)
                    
                    _exportState.value = ExportState.Exporting(
                        (index.toFloat() / ids.size), 
                        texts.vmExportGenerating(workout.activityName, index + 1, ids.size)
                    )

                    val gpxContent = gpxGenerator.generateGpx(workout, points)
                    val baseName = "${workout.activityName}_${sdf.format(Date(workout.startTime))}"
                        .replace(" ", "_")
                        .replace(":", "")
                    
                    val count = usedFileNames.getOrDefault(baseName, 0)
                    val fileName = if (count == 0) "$baseName.gpx" else "${baseName}_${count + 1}.gpx"
                    usedFileNames[baseName] = count + 1
                    
                    val file = File(exportDir, fileName)
                    file.writeText(gpxContent)
                    generatedFiles.add(file)
                }

                if (generatedFiles.isEmpty()) {
                    _exportState.value = ExportState.Error(texts.VM_EXPORT_NO_FILES)
                    return@launch
                }

                if (generatedFiles.size == 1) {
                    val uri = FileProvider.getUriForFile(context, "com.example.sportapp.fileprovider", generatedFiles[0])
                    _exportState.value = ExportState.Success(uri, false)
                } else {
                    _exportState.value = ExportState.Exporting(0.9f, texts.VM_EXPORT_ZIPPING)
                    val zipFile = File(exportDir, "SportApp_Export_${sdf.format(Date())}.zip")
                    ZipManager().zipFiles(generatedFiles, zipFile)
                    val uri = FileProvider.getUriForFile(context, "com.example.sportapp.fileprovider", zipFile)
                    _exportState.value = ExportState.Success(uri, true)
                }

            } catch (e: Exception) {
                _exportState.value = ExportState.Error(texts.vmExportError(e.message ?: "Unknown error"))
            }
        }
    }

    private suspend fun getTexts(): MobileTexts {
        return settingsManager.settingsFlow.first().language.texts
    }

    fun resetExportState() {
        _exportState.value = ExportState.Idle
    }

    fun importGpx(uri: Uri, definition: WorkoutDefinition) {
        viewModelScope.launch {
            _importState.value = ImportState.Loading
            val texts = getTexts()
            try {
                val settings = settingsManager.settingsFlow.first()
                val result = withContext(Dispatchers.IO) {
                    context.contentResolver.openInputStream(uri)?.use { input ->
                        gpxImporter.importGpx(input, definition, settings.healthData, texts)
                    } ?: throw Exception(texts.VM_IMPORT_OPEN_ERROR)
                }

                val isDuplicate = checkDuplicate(result.workout.startTime, result.workout.durationSeconds)
                val warnings = result.warnings.toMutableList()
                if (isDuplicate) {
                    warnings.add(0, texts.VM_IMPORT_DUPLICATE_WARNING)
                }

                if (warnings.isNotEmpty()) {
                    _importState.value = ImportState.Warning(warnings) {
                        saveImportedWorkout(result)
                    }
                } else {
                    saveImportedWorkout(result)
                }
            } catch (e: Exception) {
                _importState.value = ImportState.Error(texts.vmImportError(e.message ?: "Unknown error"))
            }
        }
    }

    private suspend fun checkDuplicate(startTime: Long, durationSeconds: Long): Boolean {
        val workouts = repository.getAllWorkouts().first()
        return workouts.any { it.startTime == startTime && it.durationSeconds == durationSeconds }
    }

    private fun saveImportedWorkout(result: GpxImporter.ImportResult) {
        viewModelScope.launch {
            val texts = getTexts()
            try {
                val workoutId = repository.insertWorkout(result.workout)
                val points = result.points.map { it.copy(workoutId = workoutId) }
                repository.insertPoints(points)
                val laps = result.laps.map { it.copy(workoutId = workoutId) }
                repository.insertLaps(laps)
                
                _importState.value = ImportState.Success(texts.VM_IMPORT_SUCCESS)
                refreshActivityTypes()
            } catch (e: Exception) {
                _importState.value = ImportState.Error(texts.vmImportError(e.message ?: "Unknown error"))
            }
        }
    }

    fun resetImportState() {
        _importState.value = ImportState.Idle
    }
}
