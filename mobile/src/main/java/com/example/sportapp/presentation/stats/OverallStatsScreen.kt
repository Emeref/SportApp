package com.example.sportapp.presentation.stats

import android.app.DatePickerDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sportapp.LocalMobileTexts
import com.example.sportapp.R
import com.example.sportapp.presentation.home.WidgetFactory
import com.example.sportapp.presentation.settings.WidgetItem
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun OverallStatsScreen(
    viewModel: OverallStatsViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToOptions: () -> Unit
) {
    val stats by viewModel.stats.collectAsStateWithLifecycle()
    val widgets by viewModel.widgets.collectAsStateWithLifecycle()
    val charts by viewModel.charts.collectAsStateWithLifecycle()
    val activityTypes by viewModel.activityTypes.collectAsStateWithLifecycle()
    val selectedTypes by viewModel.selectedTypes.collectAsStateWithLifecycle()
    val startDate by viewModel.startDate.collectAsStateWithLifecycle()
    val endDate by viewModel.endDate.collectAsStateWithLifecycle()
    val chartMaxValues by viewModel.chartMaxValues.collectAsStateWithLifecycle()

    OverallStatsContent(
        stats = stats,
        widgets = widgets,
        charts = charts,
        activityTypes = activityTypes,
        selectedTypes = selectedTypes,
        startDate = startDate,
        endDate = endDate,
        chartProducers = viewModel.chartProducers,
        chartMaxValues = chartMaxValues,
        onTypeToggle = { viewModel.toggleTypeSelection(it) },
        onToggleAllTypes = { viewModel.toggleAllTypes() },
        onDateRangeSelected = { start, end -> viewModel.onDateRangeSelected(start, end) },
        onNavigateBack = {
             // Zabezpieczenie przed błędem IndexOutOfBoundsException przy szybkim powrocie
             // Może on wynikać z trwającej animacji Predictive Back w NavHost
             try {
                onNavigateBack()
             } catch (e: Exception) {
                // Log i zignorowanie, aby nie wywalało apki przy powrocie
                android.util.Log.e("OverallStatsScreen", "Error during navigation back", e)
             }
        },
        onNavigateToOptions = onNavigateToOptions
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverallStatsContent(
    stats: Map<String, Any>,
    widgets: List<WidgetItem>,
    charts: List<WidgetItem>,
    activityTypes: List<String>,
    selectedTypes: Set<String>?,
    startDate: Date?,
    endDate: Date?,
    chartProducers: Map<String, ChartEntryModelProducer>,
    chartMaxValues: Map<String, Double>,
    onTypeToggle: (String) -> Unit,
    onToggleAllTypes: () -> Unit,
    onDateRangeSelected: (Date?, Date?) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToOptions: () -> Unit
) {
    val texts = LocalMobileTexts.current
    var showTypeMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.logo_apki_biale),
                            contentDescription = null,
                            modifier = Modifier.size(32.dp).padding(end = 8.dp)
                        )
                        Text(texts.STATS_TITLE)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = texts.SETTINGS_CLOSE)
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToOptions) {
                        Icon(Icons.Default.Settings, contentDescription = texts.HOME_OPTIONS)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // 1. Filtry
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(texts.STATS_FILTERS, style = MaterialTheme.typography.titleSmall)
                    
                    Box {
                        OutlinedButton(
                            onClick = { showTypeMenu = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = when {
                                    selectedTypes == null -> texts.STATS_ALL_TYPES
                                    selectedTypes.isEmpty() -> texts.STATS_ALL_TYPES // Should not happen with null logic but for safety
                                    selectedTypes.size == 1 -> selectedTypes.first()
                                    else -> "${texts.ACTIVITY_FILTERS}: ${selectedTypes.size}"
                                }
                            )
                            Icon(Icons.Default.ArrowDropDown, null)
                        }
                        DropdownMenu(
                            expanded = showTypeMenu, 
                            onDismissRequest = { showTypeMenu = false },
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            DropdownMenuItem(
                                text = { 
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Checkbox(checked = selectedTypes == null, onCheckedChange = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(texts.ACTIVITY_ALL)
                                    }
                                },
                                onClick = { onToggleAllTypes() }
                            )
                            activityTypes.forEach { type ->
                                DropdownMenuItem(
                                    text = { 
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Checkbox(checked = selectedTypes?.contains(type) ?: true, onCheckedChange = null)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(type)
                                        }
                                    },
                                    onClick = { onTypeToggle(type) }
                                )
                            }
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = {
                                val cal = Calendar.getInstance()
                                startDate?.let { cal.time = it }
                                DatePickerDialog(context, { _, y, m, d ->
                                    val date = Calendar.getInstance().apply { 
                                        set(y, m, d, 0, 0, 0)
                                        set(Calendar.MILLISECOND, 0)
                                    }.time
                                    onDateRangeSelected(date, endDate)
                                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.CalendarToday, null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(startDate?.let { sdf.format(it) } ?: texts.STATS_FROM)
                        }

                        OutlinedButton(
                            onClick = {
                                val cal = Calendar.getInstance()
                                endDate?.let { cal.time = it }
                                DatePickerDialog(context, { _, y, m, d ->
                                    val date = Calendar.getInstance().apply { 
                                        set(y, m, d, 23, 59, 59)
                                        set(Calendar.MILLISECOND, 999)
                                    }.time
                                    onDateRangeSelected(startDate, date)
                                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.CalendarToday, null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(endDate?.let { sdf.format(it) } ?: texts.STATS_TO)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2. Widgety
            val activeWidgets = widgets.filter { it.isEnabled }
            if (activeWidgets.isEmpty()) {
                Text(texts.STATS_NO_WIDGETS)
            } else {
                activeWidgets.chunked(2).forEach { rowItems ->
                    if (rowItems.size == 2) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            WidgetFactory(rowItems[0].id, stats, Modifier.weight(1f))
                            WidgetFactory(rowItems[1].id, stats, Modifier.weight(1f))
                        }
                    } else {
                        WidgetFactory(rowItems[0].id, stats, Modifier.fillMaxWidth())
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3. Wykresy
            val activeCharts = charts.filter { it.isEnabled }
            @Suppress("UNCHECKED_CAST")
            val rawData = stats["raw_data"] as? List<Any>
            if (activeCharts.isNotEmpty()) {
                if (!rawData.isNullOrEmpty()) {
                    Text(
                        text = texts.STATS_TREND_CHARTS,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                    )
                    
                    activeCharts.forEach { chart ->
                        val producer = chartProducers[chart.id]
                        if (producer != null) {
                            val maxVal = chartMaxValues[chart.id] ?: 0.0
                            val unit = when(chart.id) {
                                "distanceGps" -> if (maxVal > 6000) texts.UNIT_KM else texts.UNIT_M
                                "distanceSteps" -> if (maxVal > 6000) texts.UNIT_KM else texts.UNIT_M
                                "calories" -> texts.UNIT_KCAL
                                "steps" -> texts.UNIT_STEPS
                                "avg_cadence" -> texts.UNIT_STEP_MIN
                                "ascent", "descent" -> texts.UNIT_M
                                "maxPressure", "minPressure" -> texts.UNIT_HPA
                                "bestPace1km" -> texts.UNIT_MIN_KM_LABEL
                                else -> ""
                            }
                            val title = when(chart.id) {
                                "distanceGps" -> texts.chartDistanceGps(maxVal > 6000)
                                "distanceSteps" -> texts.chartDistanceSteps(maxVal > 6000)
                                "steps" -> texts.CHART_STEPS
                                else -> texts.getSensorLabel(chart.id)
                            }
                            CommonChartSection(
                                title = title,
                                producer = producer,
                                unit = unit,
                                overallRawData = rawData,
                                isScrollEnabled = true,
                                isZoomEnabled = true,
                                isTimestampX = true
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                } else if (stats.containsKey("raw_data")) {
                    Text(
                        text = texts.STATS_NO_DATA,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Image(
                painter = painterResource(id = R.drawable.logo_apki_biale),
                contentDescription = texts.HOME_LOGO_DESC,
                modifier = Modifier.height(40.dp).padding(vertical = 8.dp)
            )
        }
    }
}
